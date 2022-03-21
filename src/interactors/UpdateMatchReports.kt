package com.kamilh.interactors

import com.kamilh.extensions.mapAsync
import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.storage.InsertMatchStatisticsError
import kotlinx.coroutines.coroutineScope

typealias UpdateMatchReports = Interactor<UpdateMatchReportParams, UpdateMatchReportResult>

data class UpdateMatchReportParams(
    val tour: Tour,
    val matches: List<MatchInfo.PotentiallyFinished>,
)

typealias UpdateMatchReportResult = Result<Unit, UpdateMatchReportError>

sealed class UpdateMatchReportError(override val message: String) : Error {
    class Network(val networkError: NetworkError) : UpdateMatchReportError("Network(networkError=${networkError.message}")
    class Insert(val error: InsertMatchStatisticsError) : UpdateMatchReportError("Insert(error=${error.message}")
}

class UpdateMatchReportInteractor(
    appDispatchers: AppDispatchers,
    private val polishLeagueRepository: PolishLeagueRepository,
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
                    polishLeagueRepository.getMatchReportId(match.id).flatMap { matchReportId ->
                        polishLeagueRepository.getMatchReport(matchReportId, tour.season).map { matchReport ->
                            match.id to matchReport
                        }
                    }
                }
        }.toResults()

        val firstFailure = callResults.firstFailure?.error
        if (firstFailure != null) {
            return Result.failure(UpdateMatchReportError.Network(firstFailure))
        }

        return matchReportPreparer(MatchReportPreparerParams(matches = callResults.values, tour = tour)).mapError {
            when (it) {
                is MatchReportPreparerError.Insert -> UpdateMatchReportError.Insert(it.error)
            }
        }
    }
}