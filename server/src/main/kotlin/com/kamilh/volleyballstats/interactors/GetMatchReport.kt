package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.interactor.Interactor
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.MatchReport
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.storage.MatchReportStorage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.tatarka.inject.annotations.Inject

typealias GetMatchReport = Interactor<MatchId, MatchReport?>

@Inject
@Singleton
class GetMatchReportInteractor(
    appDispatchers: AppDispatchers,
    private val matchReportStorage: MatchReportStorage,
) : GetMatchReport(appDispatchers) {

    private val mapCache = mutableMapOf<MatchId, MatchReport>()
    private val mutex = Mutex()

    override suspend fun doWork(params: MatchId): MatchReport? =
        mutex.withLock {
            mapCache[params]
        } ?: matchReportStorage.getMatchReport(params)?.also { saved ->
            mutex.withLock { mapCache[params] = saved }
        }
}
