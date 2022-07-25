package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.extensions.mapAsync
import com.kamilh.volleyballstats.models.PlayerWithDetails
import com.kamilh.volleyballstats.models.toPlayer
import com.kamilh.volleyballstats.network.NetworkError
import com.kamilh.volleyballstats.repository.polishleague.PolishLeagueRepository
import com.kamilh.volleyballstats.storage.InsertPlayerError
import com.kamilh.volleyballstats.storage.PlayerStorage
import kotlinx.coroutines.coroutineScope
import me.tatarka.inject.annotations.Inject

typealias UpdatePlayers = Interactor<UpdatePlayersParams, UpdatePlayersResult>

data class UpdatePlayersParams(val tour: Tour)

typealias UpdatePlayersResult = Result<Unit, UpdatePlayersError>

sealed class UpdatePlayersError(override val message: String) : Error {
    class Network(val networkError: NetworkError) : UpdatePlayersError("Network(networkError: ${networkError.message})")
    class Storage(val insertPlayerError: InsertPlayerError) : UpdatePlayersError("Storage(insertPlayerError: ${insertPlayerError.message})")
}

@Inject
class UpdatePlayersInteractor(
    appDispatchers: AppDispatchers,
    private val polishLeagueRepository: PolishLeagueRepository,
    private val playerStorage: PlayerStorage,
) : UpdatePlayers(appDispatchers) {

    override suspend fun doWork(params: UpdatePlayersParams): UpdatePlayersResult {
        val playersResult = polishLeagueRepository.getAllPlayers(params.tour.season)
        if (playersResult is Result.Failure) {
            return playersResult.mapError { UpdatePlayersError.Network(it) }
        }
        val players = playersResult.value!!
        val playersWithDetails = coroutineScope {
            players.mapAsync(this) { player ->
                polishLeagueRepository.getPlayerDetails(params.tour.season, player.id).map { details ->
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
        playerStorage.insert(players.map { it.toPlayer() }, params.tour.id)
            .mapError { UpdatePlayersError.Storage(it) }
}
