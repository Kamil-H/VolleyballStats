package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.interactor.NoInputInteractor
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.storage.MatchStorage
import com.kamilh.volleyballstats.storage.TourStorage
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject

typealias CacheInvalidator = NoInputInteractor<Unit>

@Inject
class CacheInvalidatorInteractor(
    appDispatchers: AppDispatchers,
    private val getMatchReport: GetMatchReport,
    private val matchStorage: MatchStorage,
    private val tourStorage: TourStorage,
) : CacheInvalidator(appDispatchers) {

    override suspend fun doWork() {
        tourStorage.getAll().first().reversed().forEach { tour ->
            matchStorage.getMatchIdsWithReport(tour.id).first().reversed().forEach { matchId ->
                getMatchReport(matchId)
            }
        }
    }
}
