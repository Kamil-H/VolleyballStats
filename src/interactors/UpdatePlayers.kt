package com.kamilh.interactors

import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import models.PlayerWithDetails

typealias UpdatePlayers = Interactor<UpdatePlayersParams, UpdatePlayersResult>

data class UpdatePlayersParams(val league: League, val tour: TourYear)

typealias UpdatePlayersResult = UnitNetworkResult

class UpdatePlayersInteractor(
    appDispatchers: AppDispatchers,
    private val polishLeagueRepository: PolishLeagueRepository,
) : UpdatePlayers(appDispatchers) {

    override suspend fun doWork(params: UpdatePlayersParams): UpdatePlayersResult = coroutineScope {
        val playersResult = polishLeagueRepository.getAllPlayers(params.tour)
        if (playersResult is Result.Failure) {
            return@coroutineScope playersResult
        }
        val players = playersResult.value!!
        val playersWithDetails = players.map { player ->
            async {
                polishLeagueRepository.getPlayerDetails(params.tour, player.id).map { details ->
                    PlayerWithDetails(player, details)
                }
            }
        }.awaitAll().toResults()
        val firstFailure = playersWithDetails.firstFailure
        if (firstFailure != null) {
            return@coroutineScope firstFailure
        }
        updatePlayers(playersWithDetails.values)
        Result.success(Unit)
    }

    private suspend fun updatePlayers(players: List<PlayerWithDetails>) {

    }
}

suspend inline fun <T, R> List<T>.mapAsync(scope: CoroutineScope, crossinline transform: suspend (T) -> R): List<R> =
    map { scope.async { transform(it) } }.awaitAll()