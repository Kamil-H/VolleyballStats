package com.kamilh.interactors

import com.kamilh.match_analyzer.MatchReportAnalyzer
import com.kamilh.match_analyzer.MatchReportAnalyzerParams
import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.storage.InsertMatchStatisticsError
import com.kamilh.storage.InsertPlayerError
import com.kamilh.storage.MatchStatisticsStorage
import com.kamilh.storage.PlayerStorage
import com.kamilh.utils.findSimilarity
import models.PlayerWithDetails
import utils.Logger

typealias MatchReportPreparer = Interactor<MatchReportPreparerParams, MatchReportPreparerResult>

data class MatchReportPreparerParams(
    val matches: List<Pair<MatchId, MatchReport>>,
    val league: League,
    val tourYear: TourYear,
)

typealias MatchReportPreparerResult = Result<Unit, MatchReportPreparerError>

sealed class MatchReportPreparerError(override val message: String? = null) : Error {
    class Network(val networkError: NetworkError) : UpdatePlayersError()
}

class MatchReportPreparerInteractor(
    appDispatchers: AppDispatchers,
    private val polishLeagueRepository: PolishLeagueRepository,
    private val playerStorage: PlayerStorage,
    private val matchReportAnalyzer: MatchReportAnalyzer,
    private val matchStatisticsStorage: MatchStatisticsStorage,
) : MatchReportPreparer(appDispatchers) {

    override suspend fun doWork(params: MatchReportPreparerParams): MatchReportPreparerResult {
        val allPlayers = polishLeagueRepository.getAllPlayers().value ?: emptyList()
        val allTeamPlayers = polishLeagueRepository.getAllPlayers(params.tourYear).value ?: emptyList()

        params.matches.forEach { (matchId, matchReport) ->
            analyze(
                matchId = matchId,
                matchReport = matchReport,
                league = params.league,
                tourYear = params.tourYear,
                allPlayers = allPlayers,
                allTeamPlayers = allTeamPlayers,
                tryFixPlayerOnError = true,
            )
        }
        return Result.success(Unit)
    }

    private suspend fun analyze(
        matchId: MatchId,
        matchReport: MatchReport,
        league: League,
        tourYear: TourYear,
        allPlayers: List<Player>,
        allTeamPlayers: List<TeamPlayer>,
        tryFixPlayerOnError: Boolean,
    ) {
        matchReportAnalyzer(MatchReportAnalyzerParams(matchReport, tourYear))
            .onSuccess { matchStatistics ->
                insert(
                    matchReport = matchReport,
                    allPlayers = allPlayers,
                    allTeamPlayers = allTeamPlayers,
                    matchStatistics = matchStatistics,
                    matchId = matchId,
                    league = league,
                    tourYear = tourYear,
                    tryFixPlayerOnError = tryFixPlayerOnError,
                )
            }
            .onFailure {
                Logger.i("AnalyzeMatchReport failure: $it")
            }
    }

    private suspend fun insert(
        matchReport: MatchReport,
        allPlayers: List<Player>,
        allTeamPlayers: List<TeamPlayer>,
        matchStatistics: MatchStatistics,
        matchId: MatchId,
        league: League,
        tourYear: TourYear,
        tryFixPlayerOnError: Boolean,
    ) {
        matchStatisticsStorage.insert(
            matchStatistics = matchStatistics,
            league = league,
            tourYear = tourYear,
            matchId = matchId,
        ).onFailure {
            when (it) {
                is InsertMatchStatisticsError.PlayerNotFound -> {
                    Logger.i("matchId: ${matchId}, matchReportId: ${matchStatistics.matchReportId}, playerIds: ${it.playerIds}")
                    if (tryFixPlayerOnError) {
                        tryUpdatePlayers(
                            matchReport = matchReport,
                            matchId = matchId,
                            allPlayers = allPlayers,
                            allTeamPlayers = allTeamPlayers,
                            playersNotFound = it.playerIds,
                            league = league,
                            tourYear = tourYear,
                        )
                    }
                }
                is InsertMatchStatisticsError.TeamNotFound, InsertMatchStatisticsError.NoPlayersInTeams,
                InsertMatchStatisticsError.TourNotFound -> { }
            }
        }
    }

    private suspend fun tryUpdatePlayers(
        matchId: MatchId,
        allPlayers: List<Player>,
        allTeamPlayers: List<TeamPlayer>,
        playersNotFound: List<Pair<PlayerId, TeamId>>,
        league: League,
        tourYear: TourYear,
        matchReport: MatchReport,
    ) {
        analyze(
            matchId = matchId,
            matchReport = matchReport.copy(
                matchTeams = matchReport.matchTeams.copy(
                    home = matchReport.matchTeams.home.fixPlayers(allPlayers, allTeamPlayers, playersNotFound, league, tourYear),
                    away = matchReport.matchTeams.away.fixPlayers(allPlayers, allTeamPlayers, playersNotFound, league, tourYear)
                )
            ),
            league = league,
            tourYear = tourYear,
            allPlayers = allPlayers,
            allTeamPlayers = allTeamPlayers,
            tryFixPlayerOnError = false,
        )
    }

    private suspend fun MatchReportTeam.fixPlayers(
        allPlayers: List<Player>,
        allTeamPlayers: List<TeamPlayer>,
        playersNotFound: List<Pair<PlayerId, TeamId>>,
        league: League,
        tourYear: TourYear,
    ): MatchReportTeam {
        val playerIds = playersNotFound.map { it.first }
        return copy(
            players = players.map { matchPlayer ->
                if (playerIds.contains(matchPlayer.id)) {
                    matchPlayer.copy(
                        id = findPlayerId(
                            allPlayers = allPlayers,
                            allTeamPlayers = allTeamPlayers,
                            matchPlayer = matchPlayer,
                            league = league,
                            tourYear = tourYear,
                            teamId = playersNotFound.first { it.first == matchPlayer.id }.second,
                        )
                    )
                } else {
                    matchPlayer
                }
            }
        )
    }

    private suspend fun findPlayerId(
        allPlayers: List<Player>,
        allTeamPlayers: List<TeamPlayer>,
        matchPlayer: MatchReportPlayer,
        league: League,
        tourYear: TourYear,
        teamId: TeamId,
    ): PlayerId {
        val playerIdFromName = allPlayers.findByName(matchPlayer)
        val player = allTeamPlayers.firstOrNull { it.id == matchPlayer.id } ?: allTeamPlayers.firstOrNull { it.id == playerIdFromName }
        Logger.i("playerIdFromName: $playerIdFromName, player: $player")
        return when {
            player != null -> getDetailsAndSave(player, league, tourYear, matchPlayer, teamId)
            else -> when (val playerWithDetails = polishLeagueRepository.getPlayerWithDetails(tourYear, playerIdFromName).value) {
                null -> matchPlayer.id
                else -> insert(playerWithDetails, league, tourYear, matchPlayer, teamId)
            }
        }
    }

    private fun List<Player>.findByName(matchPlayer: MatchReportPlayer): PlayerId =
        firstOrNull { player ->
            player.name.contains(matchPlayer.firstName) && player.name.contains(matchPlayer.lastName)
        }?.id ?: firstOrNull { player ->
            val matchPlayerFullName = "${matchPlayer.firstName} ${matchPlayer.lastName}"
            (matchPlayerFullName.findSimilarity(player.name) >= NAME_SIMILARITY_THRESHOLD).apply {
                if (this) {
                    Logger.i("Found similarity: MatchPlayer: $matchPlayerFullName and Player: ${player.name}")
                }
            }
        }?.id ?: matchPlayer.id

    private suspend fun getDetailsAndSave(
        player: TeamPlayer,
        league: League,
        tourYear: TourYear,
        matchPlayer: MatchReportPlayer,
        teamId: TeamId,
    ): PlayerId =
        polishLeagueRepository.getPlayerDetails(tourYear, player.id)
            .map { playerDetails ->
                insert(
                    playerWithDetails = PlayerWithDetails(teamPlayer = player, details = playerDetails),
                    league = league,
                    tourYear = tourYear,
                    matchPlayer = matchPlayer,
                    teamId = teamId,
                )
            }.value ?: player.id

    private suspend fun insert(
        playerWithDetails: PlayerWithDetails,
        league: League,
        tourYear: TourYear,
        matchPlayer: MatchReportPlayer,
        teamId: TeamId,
    ): PlayerId {
        if (teamId != playerWithDetails.teamPlayer.team) {
            Logger.i("Detected new team (${teamId.value}) for the player: ${playerWithDetails.teamPlayer.name} (${playerWithDetails.teamPlayer.id})")
        }
        if (matchPlayer.shirtNumber != playerWithDetails.details.number) {
            Logger.i("Detected new shirt number (${matchPlayer.shirtNumber}) for the player: ${playerWithDetails.teamPlayer.name} ${playerWithDetails.teamPlayer.id}")
        }
        playerStorage.insert(
            players = listOf(
                playerWithDetails.copy(
                    teamPlayer = playerWithDetails.teamPlayer.copy(
                        team = teamId,
                    ),
                    details = playerWithDetails.details.copy(
                        number = matchPlayer.shirtNumber,
                    )
                )
            ),
            league = league,
            tour = tourYear,
        ).onFailure { error ->
            val message = when (error) {
                is InsertPlayerError.Errors -> buildString {
                    append("Player already inserted: ${playerWithDetails.teamPlayer} ")
                        .takeIf { error.teamPlayersAlreadyExists.isNotEmpty() }
                    append("Team not found: ${error.teamsNotFound.joinToString { it.value.toString() }}")
                        .takeIf { error.teamsNotFound.isNotEmpty() }
                }
                InsertPlayerError.TourNotFound -> "Tour not found"
            }
            Logger.i(message)
        }
        return playerWithDetails.teamPlayer.id
    }

    companion object {
        private const val NAME_SIMILARITY_THRESHOLD = 0.7
    }
}