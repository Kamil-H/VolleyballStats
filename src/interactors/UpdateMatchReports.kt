package com.kamilh.interactors

import com.kamilh.match_analyzer.MatchReportAnalyzer
import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.storage.InsertMatchStatisticsError
import com.kamilh.storage.MatchStatisticsStorage
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
    private val matchReportAnalyzer: MatchReportAnalyzer,
    private val matchStatisticsStorage: MatchStatisticsStorage,
    private val polishLeagueRepository: PolishLeagueRepository,
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

        val insertResults = callResults.values
            .map { (matchId, matchReportId) ->
                matchId to matchReportAnalyzer.analyze(matchReportId, tourYear, league)
            }
            .map { (matchId, matchReport) ->
                matchStatisticsStorage.insert(
                    matchStatistics = matchReport,
                    league = league,
                    tourYear = tourYear,
                    matchId = matchId,
                )
            }.toResults()

        val insertFailures = insertResults.failures
        val teamsNotFound = insertFailures
            .filterIsInstance<InsertMatchStatisticsError.TeamNotFound>()
            .map { it.teamId }

        if (teamsNotFound.isNotEmpty()) {
            return Result.failure(UpdateMatchReportError.TeamsNotFound(teamsNotFound))
        }

        val playersNotFound = insertFailures
            .filterIsInstance<InsertMatchStatisticsError.PlayerNotFound>()
            .flatMap { it.playerIds }

        if (playersNotFound.isNotEmpty()) {
            return Result.failure(UpdateMatchReportError.PlayersNotFound(playersNotFound))
        }
        return Result.success(Unit)
    }
}