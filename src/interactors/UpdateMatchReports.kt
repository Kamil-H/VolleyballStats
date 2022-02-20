package com.kamilh.interactors

import com.kamilh.extensions.mapAsync
import com.kamilh.match_analyzer.MatchReportAnalyzerError
import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.storage.InsertMatchStatisticsError
import kotlinx.coroutines.coroutineScope

typealias UpdateMatchReports = Interactor<UpdateMatchReportParams, UpdateMatchReportResult>

data class UpdateMatchReportParams(
    val league: League,
    val tour: TourYear,
    val matches: List<AllMatchesItem.PotentiallyFinished>,
)

typealias UpdateMatchReportResult = Result<Unit, UpdateMatchReportError>

sealed class UpdateMatchReportError(override val message: String? = null) : Error {
    class Network(val networkError: NetworkError) : UpdateMatchReportError()
    class Analyze(val error: MatchReportAnalyzerError) : UpdateMatchReportError()
    class Insert(val error: InsertMatchStatisticsError) : UpdateMatchReportError()
}

class UpdateMatchReportInteractor(
    appDispatchers: AppDispatchers,
    private val polishLeagueRepository: PolishLeagueRepository,
    private val matchReportPreparer: MatchReportPreparer,
): UpdateMatchReports(appDispatchers) {

    override suspend fun doWork(params: UpdateMatchReportParams): UpdateMatchReportResult {
        val (league, tourYear, potentiallyFinished) = params

        if (potentiallyFinished.isEmpty()) {
            return Result.success(Unit)
        }
        val callResults = coroutineScope {
            potentiallyFinished
                .mapAsync(scope = this) { match ->
                    polishLeagueRepository.getMatchReportId(match.id).flatMap { matchReportId ->
                        polishLeagueRepository.getMatchReport(matchReportId, tourYear).map { matchReport ->
                            match.id to matchReport
                        }
                    }
                }
        }.toResults()

        val firstFailure = callResults.firstFailure?.error
        if (firstFailure != null) {
            return Result.failure(UpdateMatchReportError.Network(firstFailure))
        }

        return matchReportPreparer(
            MatchReportPreparerParams(
                matches = callResults.values,
                league = league,
                tourYear = tourYear,
            )
        ).mapError {
            when (it) {
                is MatchReportPreparerError.Analyze -> UpdateMatchReportError.Analyze(it.error)
                is MatchReportPreparerError.Insert -> UpdateMatchReportError.Insert(it.error)
            }
        }
    }
}