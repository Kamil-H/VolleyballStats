package com.kamilh.interactors

import com.kamilh.extensions.mapAsync
import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.storage.InsertPlayerError
import com.kamilh.storage.PlayerStorage
import kotlinx.coroutines.coroutineScope
import models.PlayerWithDetails

typealias UpdatePlayers = Interactor<UpdatePlayersParams, UpdatePlayersResult>

data class UpdatePlayersParams(val league: League, val season: Season)

typealias UpdatePlayersResult = Result<Unit, UpdatePlayersError>

sealed class UpdatePlayersError(override val message: String) : Error {
    class Network(val networkError: NetworkError) : UpdatePlayersError("Network(networkError: ${networkError.message})")
    class Storage(val insertPlayerError: InsertPlayerError) : UpdatePlayersError("Storage(insertPlayerError: ${insertPlayerError.message})")
}

class UpdatePlayersInteractor(
    appDispatchers: AppDispatchers,
    private val polishLeagueRepository: PolishLeagueRepository,
    private val playerStorage: PlayerStorage,
) : UpdatePlayers(appDispatchers) {

    override suspend fun doWork(params: UpdatePlayersParams): UpdatePlayersResult {
        val playersResult = polishLeagueRepository.getAllPlayers(params.season)
        if (playersResult is Result.Failure) {
            return playersResult.mapError { UpdatePlayersError.Network(it) }
        }
        val players = playersResult.value!!
        val playersWithDetails = coroutineScope {
            players.mapAsync(this) { player ->
                polishLeagueRepository.getPlayerDetails(params.season, player.id).map { details ->
                    PlayerWithDetails(player, details)
                }
            }.toResults()
        }
        val firstFailure = playersWithDetails.firstFailure
        if (firstFailure != null) {
            return firstFailure.mapError { UpdatePlayersError.Network(it) }
        }
        return updatePlayers(playersWithDetails.values, params)
    }

    private suspend fun updatePlayers(players: List<PlayerWithDetails>, params: UpdatePlayersParams): UpdatePlayersResult =
        playerStorage.insert(players, params.league, params.season)
            .mapError { UpdatePlayersError.Storage(it) }
}