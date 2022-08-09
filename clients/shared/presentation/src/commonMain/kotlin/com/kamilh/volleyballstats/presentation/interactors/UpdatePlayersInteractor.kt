package com.kamilh.volleyballstats.presentation.interactors

import com.kamilh.volleyballstats.clients.data.StatsRepository
import com.kamilh.volleyballstats.domain.models.flatMap
import com.kamilh.volleyballstats.domain.models.mapError
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.interactors.UpdatePlayers
import com.kamilh.volleyballstats.interactors.UpdatePlayersError
import com.kamilh.volleyballstats.interactors.UpdatePlayersParams
import com.kamilh.volleyballstats.interactors.UpdatePlayersResult
import com.kamilh.volleyballstats.storage.PlayerStorage
import me.tatarka.inject.annotations.Inject

@Inject
class UpdatePlayersInteractor(
    appDispatchers: AppDispatchers,
    private val statsRepository: StatsRepository,
    private val playerStorage: PlayerStorage,
) : UpdatePlayers(appDispatchers) {

    override suspend fun doWork(params: UpdatePlayersParams): UpdatePlayersResult =
        statsRepository.getPlayers(params.tour)
            .mapError { UpdatePlayersError.Network(it) }
            .flatMap { players ->
                playerStorage.insert(players, params.tour.id).mapError {
                    UpdatePlayersError.Storage(it)
                }
            }
}
