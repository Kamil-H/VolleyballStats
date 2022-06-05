package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.match_analyzer.MatchReportAnalyzer
import com.kamilh.volleyballstats.match_analyzer.MatchReportAnalyzerParams
import com.kamilh.volleyballstats.models.MatchReport
import com.kamilh.volleyballstats.models.MatchReportTeam
import com.kamilh.volleyballstats.storage.InsertMatchStatisticsError
import com.kamilh.volleyballstats.storage.MatchStatisticsStorage
import com.kamilh.volleyballstats.domain.utils.Logger
import me.tatarka.inject.annotations.Inject

typealias MatchReportPreparer = Interactor<MatchReportPreparerParams, MatchReportPreparerResult>

data class MatchReportPreparerParams(
    val matches: List<Pair<MatchId, MatchReport>>,
    val tour: Tour
)

typealias MatchReportPreparerResult = Result<Unit, MatchReportPreparerError>

sealed class MatchReportPreparerError(override val message: String) : Error {
    class Insert(val error: InsertMatchStatisticsError) : MatchReportPreparerError("Insert(error=${error.message})")
}

@Inject
class MatchReportPreparerInteractor(
    appDispatchers: AppDispatchers,
    private val matchReportAnalyzer: MatchReportAnalyzer,
    private val matchStatisticsStorage: MatchStatisticsStorage,
    private val fixWrongPlayers: FixWrongPlayers,
) : MatchReportPreparer(appDispatchers) {

    override suspend fun doWork(params: MatchReportPreparerParams): MatchReportPreparerResult =
        params.matches.map { (matchId, matchReport) ->
            analyze(
                matchId = matchId,
                matchReport = matchReport,
                tour = params.tour,
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
        tour: Tour,
        tryFixPlayerOnError: Boolean,
    ): MatchReportPreparerResult =
        matchReportAnalyzer(MatchReportAnalyzerParams(matchId, matchReport, tour))
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
                        tour = tour,
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
        tour: Tour,
        tryFixPlayerOnError: Boolean,
    ): MatchReportPreparerResult =
        matchStatisticsStorage.insert(
            matchStatistics = matchStatistics,
            tourId = tour.id,
        ).flatMapError {
            when (it) {
                is InsertMatchStatisticsError.PlayerNotFound -> {
                    Logger.i("matchId: ${matchId}, matchReportId: ${matchStatistics.matchId}, playerIds: ${it.playerIds}")
                    if (tryFixPlayerOnError) {
                        tryUpdatePlayers(
                            matchReport = matchReport,
                            matchId = matchId,
                            playersNotFound = it.playerIds,
                            tour = tour,
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
        tour: Tour,
        matchReport: MatchReport,
    ) : MatchReportPreparerResult =
        analyze(
            matchId = matchId,
            matchReport = matchReport.copy(
                matchTeams = matchReport.matchTeams.copy(
                    home = matchReport.matchTeams.home.fixPlayers(playersNotFound, tour),
                    away = matchReport.matchTeams.away.fixPlayers(playersNotFound, tour),
                )
            ),
            tour = tour,
            tryFixPlayerOnError = false,
        )

    private suspend fun MatchReportTeam.fixPlayers(
        playersNotFound: List<Pair<PlayerId, TeamId>>,
        tour: Tour,
    ): MatchReportTeam = fixWrongPlayers(
        FixWrongPlayersParams(
            team = this,
            playersNotFound = playersNotFound,
            tour = tour,
        )
    )
}