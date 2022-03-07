package com.kamilh.interactors

import com.kamilh.match_analyzer.MatchReportAnalyzer
import com.kamilh.match_analyzer.MatchReportAnalyzerParams
import com.kamilh.models.*
import com.kamilh.storage.InsertMatchStatisticsError
import com.kamilh.storage.MatchStatisticsStorage
import utils.Logger

typealias MatchReportPreparer = Interactor<MatchReportPreparerParams, MatchReportPreparerResult>

data class MatchReportPreparerParams(
    val matches: List<Pair<MatchId, MatchReport>>,
    val league: League,
    val season: Season,
)

typealias MatchReportPreparerResult = Result<Unit, MatchReportPreparerError>

sealed class MatchReportPreparerError(override val message: String) : Error {
    class Insert(val error: InsertMatchStatisticsError) : MatchReportPreparerError("Insert(error=${error.message})")
}

class MatchReportPreparerInteractor(
    appDispatchers: AppDispatchers,
    private val matchReportAnalyzer: MatchReportAnalyzer,
    private val matchStatisticsStorage: MatchStatisticsStorage,
    private val wrongPlayerFixer: WrongPlayerFixer,
) : MatchReportPreparer(appDispatchers) {

    override suspend fun doWork(params: MatchReportPreparerParams): MatchReportPreparerResult =
        params.matches.map { (matchId, matchReport) ->
            analyze(
                matchId = matchId,
                matchReport = matchReport,
                league = params.league,
                tourYear = params.season,
                tryFixPlayerOnError = true,
            )
        }.toResults().apply {
            Logger.i(
                message = "Matches to analyze: ${params.matches.size}, " +
                        "Correctly analyzed: ${this.successes.size}, " +
                        "Errors: ${this.failures.size}"
            )
        }.toResult() ?: Result.success(Unit)

    private suspend fun analyze(
        matchId: MatchId,
        matchReport: MatchReport,
        league: League,
        tourYear: Season,
        tryFixPlayerOnError: Boolean,
    ): MatchReportPreparerResult =
        matchReportAnalyzer(MatchReportAnalyzerParams(matchReport, tourYear))
            .flatMapError {
                Logger.i("AnalyzeMatchReport failure: ${it.message}")
                // Ignoring analyze error. It should be reported and proceeded further
                Result.success<MatchStatistics?, MatchReportPreparerError>(null)
            }
            .flatMap { matchStatistics ->
                if (matchStatistics != null) {
                    insert(
                        matchReport = matchReport,
                        matchStatistics = matchStatistics,
                        matchId = matchId,
                        league = league,
                        tourYear = tourYear,
                        tryFixPlayerOnError = tryFixPlayerOnError,
                    )
                } else {
                    Result.success(Unit)
                }
            }

    private suspend fun insert(
        matchReport: MatchReport,
        matchStatistics: MatchStatistics,
        matchId: MatchId,
        league: League,
        tourYear: Season,
        tryFixPlayerOnError: Boolean,
    ): MatchReportPreparerResult =
        matchStatisticsStorage.insert(
            matchStatistics = matchStatistics,
            league = league,
            season = tourYear,
            matchId = matchId,
        ).flatMapError {
            when (it) {
                is InsertMatchStatisticsError.PlayerNotFound -> {
                    Logger.i("matchId: ${matchId}, matchReportId: ${matchStatistics.matchReportId}, playerIds: ${it.playerIds}")
                    if (tryFixPlayerOnError) {
                        tryUpdatePlayers(
                            matchReport = matchReport,
                            matchId = matchId,
                            playersNotFound = it.playerIds,
                            league = league,
                            tourYear = tourYear,
                        )
                    } else {
                        MatchReportPreparerResult.failure(MatchReportPreparerError.Insert(it))
                    }
                }
                is InsertMatchStatisticsError.TeamNotFound, InsertMatchStatisticsError.NoPlayersInTeams,
                InsertMatchStatisticsError.TourNotFound -> MatchReportPreparerResult.failure(
                    MatchReportPreparerError.Insert(it)
                )
            }
        }

    private suspend fun tryUpdatePlayers(
        matchId: MatchId,
        playersNotFound: List<Pair<PlayerId, TeamId>>,
        league: League,
        tourYear: Season,
        matchReport: MatchReport,
    ) : MatchReportPreparerResult =
        analyze(
            matchId = matchId,
            matchReport = matchReport.copy(
                matchTeams = matchReport.matchTeams.copy(
                    home = matchReport.matchTeams.home.fixPlayers(playersNotFound, league, tourYear),
                    away = matchReport.matchTeams.away.fixPlayers(playersNotFound, league, tourYear),
                )
            ),
            league = league,
            tourYear = tourYear,
            tryFixPlayerOnError = false,
        )

    private suspend fun MatchReportTeam.fixPlayers(
        playersNotFound: List<Pair<PlayerId, TeamId>>,
        league: League,
        tourYear: Season,
    ): MatchReportTeam = wrongPlayerFixer(
        WrongPlayerFixerParams(
            team = this,
            playersNotFound = playersNotFound,
            league = league,
            tourYear = tourYear,
        )
    )
}