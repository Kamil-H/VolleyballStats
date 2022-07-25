package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.extensions.mapAsync
import com.kamilh.volleyballstats.network.NetworkError
import com.kamilh.volleyballstats.repository.polishleague.PolishLeagueRepository
import com.kamilh.volleyballstats.storage.InsertMatchReportError
import kotlinx.coroutines.coroutineScope
import me.tatarka.inject.annotations.Inject

typealias UpdateMatchReports = Interactor<UpdateMatchReportParams, UpdateMatchReportResult>

data class UpdateMatchReportParams(
    val tour: Tour,
    val matches: List<MatchInfo.PotentiallyFinished>,
)

typealias UpdateMatchReportResult = Result<Unit, UpdateMatchReportError>

sealed class UpdateMatchReportError(override val message: String) : Error {
    class Network(val networkError: NetworkError) : UpdateMatchReportError("Network(networkError=${networkError.message}")
    class Insert(val error: InsertMatchReportError) : UpdateMatchReportError("Insert(error=${error.message}")
}

@Inject
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
