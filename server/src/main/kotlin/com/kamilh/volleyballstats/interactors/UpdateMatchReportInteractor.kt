package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.extensions.mapAsync
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.repository.polishleague.PlsRepository
import kotlinx.coroutines.coroutineScope
import me.tatarka.inject.annotations.Inject

@Inject
class UpdateMatchReportInteractor(
    appDispatchers: AppDispatchers,
    private val polishLeagueRepository: PlsRepository,
    private val matchReportPreparer: MatchReportPreparer,
): UpdateMatchReports(appDispatchers) {

    override suspend fun doWork(params: UpdateMatchReportParams): UpdateMatchReportResult {
        val (tour, potentiallyFinished) = params

        if (potentiallyFinished.isEmpty()) {
            return Result.success(Unit)
        }
        val callResults = coroutineScope {
            potentiallyFinished
                .mapAsync(scope = this) { match ->
                    polishLeagueRepository.getMatchReportId(match).flatMap { matchReportId ->
                        polishLeagueRepository.getMatchReport(matchReportId, tour.season).map { matchReport ->
                            match to matchReport
                        }
                    }
                }
        }.toResults()

        val matchReportResults = matchReportPreparer(
            MatchReportPreparerParams(matches = callResults.values, tour = tour)
        )
        val callFailures = callResults.failures

        return if (matchReportResults.error != null || callFailures.isNotEmpty()) {
            Result.failure(
                UpdateMatchReportError(
                    networkErrors = callFailures.map { it.error },
                    insertErrors = listOfNotNull(
                        (matchReportResults.error as? MatchReportPreparerError.Insert)?.error
                    ),
                )
            )
        } else {
            Result.success(Unit)
        }
    }
}
