package com.kamilh.volleyballstats.presentation.interactors

import com.kamilh.volleyballstats.clients.data.StatsRepository
import com.kamilh.volleyballstats.domain.extensions.mapAsync
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.interactors.UpdateMatchReportError
import com.kamilh.volleyballstats.interactors.UpdateMatchReportParams
import com.kamilh.volleyballstats.interactors.UpdateMatchReportResult
import com.kamilh.volleyballstats.interactors.UpdateMatchReports
import com.kamilh.volleyballstats.storage.MatchReportStorage
import kotlinx.coroutines.coroutineScope
import me.tatarka.inject.annotations.Inject

@Inject
class UpdateMatchReportInteractor(
    appDispatchers: AppDispatchers,
    private val statsRepository: StatsRepository,
    private val matchReportStorage: MatchReportStorage,
): UpdateMatchReports(appDispatchers) {

    override suspend fun doWork(params: UpdateMatchReportParams): UpdateMatchReportResult {
        val (tour, potentiallyFinished) = params

        if (potentiallyFinished.isEmpty()) {
            return Result.success(Unit)
        }
        val callResults = coroutineScope {
            potentiallyFinished.mapAsync(scope = this) { match ->
                statsRepository.getMatchReport(match)
            }
        }.toResults()

        val firstFailure = callResults.firstFailure?.error
        if (firstFailure != null) {
            return Result.failure(UpdateMatchReportError.Network(firstFailure))
        }

        return callResults.values.map { matchReport ->
            matchReportStorage.insert(matchReport, tour.id).mapError(UpdateMatchReportError::Insert)
        }.toResults().toResult() ?: UpdateMatchReportResult.success(Unit)
    }
}
