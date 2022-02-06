package com.kamilh.interactors

import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.storage.InsertMatchStatisticsResult
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
    class TeamsNotFound(val teamIds: List<TeamId>) : UpdateMatchReportError()
    class PlayersNotFound(val playerIds: List<PlayerId>) : UpdateMatchReportError()
}

class UpdateMatchReportInteractor(
    appDispatchers: AppDispatchers,
    private val polishLeagueRepository: PolishLeagueRepository,
    private val matchReportPreparer: MatchReportPreparer,
): UpdateMatchReports(appDispatchers) {

    override suspend fun doWork(params: UpdateMatchReportParams): UpdateMatchReportResult {
        val tourYear = params.tour
        val league = params.league
        val potentiallyFinished = params.matches

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

        matchReportPreparer(
            MatchReportPreparerParams(
                matches = callResults.values,
                league = league,
                tourYear = tourYear,
            )
        )

//        val insertResults = callResults.values
//            .map { (matchId, matchReportId) ->
//                matchId to matchReportAnalyzer.analyze(matchReportId, tourYear, league)
//            }
//            .map { (matchId, matchReport) ->
//                InsertResult(
//                    result = matchStatisticsStorage.insert(
//                        matchStatistics = matchReport,
//                        league = league,
//                        tourYear = tourYear,
//                        matchId = matchId,
//                    ),
//                    matchId = matchId,
//                    matchReportId = matchReport.matchReportId,
//                )
//            }
//
//        val insertFailures = insertResults.map { it.result }.toResults().failures
//        val teamsNotFound = insertFailures
//            .filterIsInstance<InsertMatchStatisticsError.TeamNotFound>()
//            .map { it.teamId }
//
//        if (teamsNotFound.isNotEmpty()) {
//            return Result.failure(UpdateMatchReportError.TeamsNotFound(teamsNotFound))
//        }
//
//        val playersNotFound = insertFailures
//            .filterIsInstance<InsertMatchStatisticsError.PlayerNotFound>()
//            .flatMap { it.playerIds }
//
//        insertResults.forEach {
//            val error = it.result.error
//            if (error is InsertMatchStatisticsError.PlayerNotFound) {
//                Logger.i("matchId: ${it.matchId}, matchReportId: ${it.matchReportId}, playerIds: ${error.playerIds}")
//            }
//        }
//        if (playersNotFound.isNotEmpty()) {
//            return Result.failure(UpdateMatchReportError.PlayersNotFound(playersNotFound))
//        }
        return Result.success(Unit)
    }

    private data class InsertResult(
        val result: InsertMatchStatisticsResult,
        val matchId: MatchId,
        val matchReportId: MatchReportId,
    )
}