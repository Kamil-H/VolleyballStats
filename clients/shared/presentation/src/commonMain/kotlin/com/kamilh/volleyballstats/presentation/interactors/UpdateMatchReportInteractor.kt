package com.kamilh.volleyballstats.presentation.interactors

import com.kamilh.volleyballstats.clients.data.StatsRepository
import com.kamilh.volleyballstats.domain.models.flatMap
import com.kamilh.volleyballstats.domain.models.mapError
import com.kamilh.volleyballstats.domain.models.toResult
import com.kamilh.volleyballstats.domain.models.toResults
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.interactors.UpdateMatchReportError
import com.kamilh.volleyballstats.interactors.UpdateMatchReportParams
import com.kamilh.volleyballstats.interactors.UpdateMatchReportResult
import com.kamilh.volleyballstats.interactors.UpdateMatchReports
import com.kamilh.volleyballstats.storage.MatchReportStorage
import me.tatarka.inject.annotations.Inject

@Inject
class UpdateMatchReportInteractor(
    appDispatchers: AppDispatchers,
    private val statsRepository: StatsRepository,
    private val matchReportStorage: MatchReportStorage,
): UpdateMatchReports(appDispatchers) {

    override suspend fun doWork(params: UpdateMatchReportParams): UpdateMatchReportResult =
        params.matches.map { match ->
            statsRepository.getMatchReport(match)
                .mapError(UpdateMatchReportError::Network)
                .flatMap {
                    matchReportStorage.insert(it, params.tour.id)
                        .mapError(UpdateMatchReportError::Insert)
                }
        }.toResults().toResult() ?: UpdateMatchReportResult.success(Unit)
}
