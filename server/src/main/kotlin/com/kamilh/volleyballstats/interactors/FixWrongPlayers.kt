package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.domain.utils.Logger
import com.kamilh.volleyballstats.models.*
import com.kamilh.volleyballstats.repository.polishleague.PolishLeagueRepository
import com.kamilh.volleyballstats.storage.InsertPlayerError
import com.kamilh.volleyballstats.storage.PlayerStorage
import com.kamilh.volleyballstats.utils.findSimilarity
import me.tatarka.inject.annotations.Inject

typealias FixWrongPlayers = Interactor<FixWrongPlayersParams, MatchReportTeam>

data class FixWrongPlayersParams(
    val team: MatchReportTeam,
    val playersNotFound: List<Pair<PlayerId, TeamId>>,
    val tour: Tour,
)

@Inject
class FixWrongPlayersInteractor(
    appDispatchers: AppDispatchers,
    private val playerStorage: PlayerStorage,
    private val polishLeagueRepository: PolishLeagueRepository,
) : FixWrongPlayers(appDispatchers) {

    override suspend fun doWork(params: FixWrongPlayersParams): MatchReportTeam {
        val allPlayers = polishLeagueRepository.getAllPlayers().value ?: emptyList()
        val allTeamPlayers = polishLeagueRepository.getAllPlayers(params.tour.season).value ?: emptyList()

        return params.team.fixPlayers(
            allPlayers = allPlayers,
            allTeamPlayers = allTeamPlayers,
            playersNotFound = params.playersNotFound,
            tour = params.tour,
        )
    }

    private suspend fun MatchReportTeam.fixPlayers(
        allPlayers: List<PlayerSnapshot>,
        allTeamPlayers: List<TeamPlayer>,
        playersNotFound: List<Pair<PlayerId, TeamId>>,
        tour: Tour,
    ): MatchReportTeam {
        val playerIds = playersNotFound.map { it.first }
        return copy(
            players = players.mapNotNull { matchPlayer ->
                if (playerIds.contains(matchPlayer.id)) {
                    val newPlayerId = findPlayerId(
                        allPlayers = allPlayers,
                        allTeamPlayers = allTeamPlayers,
                        matchPlayer = matchPlayer,
                        tour = tour,
                        teamId = playersNotFound.first { it.first == matchPlayer.id }.second,
                    )
                    if (newPlayerId != null) {
                        matchPlayer.copy(id = newPlayerId)
                    } else {
                        log("Player not found, removing player: ${matchPlayer.id}")
                        null
                    }
                } else {
                    matchPlayer
                }
            }
        )
    }

    private suspend fun findPlayerId(
        allPlayers: List<PlayerSnapshot>,
        allTeamPlayers: List<TeamPlayer>,
        matchPlayer: MatchReportPlayer,
        tour: Tour,
        teamId: TeamId,
    ): PlayerId? {
        val playerIdFromName = allPlayers.findByName(matchPlayer)
        val player = allTeamPlayers.firstOrNull { it.id == matchPlayer.id } ?: allTeamPlayers.firstOrNull { it.id == playerIdFromName }
        log("playerIdFromName: $playerIdFromName, player: $player")
        return when {
            player != null -> getDetailsAndSave(player, tour, matchPlayer, teamId)
            playerIdFromName != null -> {
                val playerWithDetails = polishLeagueRepository.getPlayerWithDetails(tour.season, playerIdFromName).value
                if (playerWithDetails != null) {
                    insert(playerWithDetails, tour, matchPlayer, teamId)
                } else {
                    matchPlayer.id
                }
            }
            else -> null
        }
    }

    private fun List<PlayerSnapshot>.findByName(matchPlayer: MatchReportPlayer): PlayerId? =
        firstOrNull { player ->
            player.name.contains(matchPlayer.firstName) && player.name.contains(matchPlayer.lastName)
        }?.id ?: firstOrNull { player ->
            player.hasSimilarName(matchPlayer).apply {
                if (this) {
                    log("Found similarity: MatchPlayer: ${"${matchPlayer.firstName} ${matchPlayer.lastName}"} and Player: ${player.name}")
                }
            }
        }?.id

    private fun PlayerSnapshot.hasSimilarName(matchPlayer: MatchReportPlayer): Boolean {
        val fullName = name.split(" ")
        return fullName[0].isSimilarTo(matchPlayer.firstName) && fullName[1].isSimilarTo(matchPlayer.lastName)
    }

    private fun String.isSimilarTo(other: String) = findSimilarity(other) >= NAME_SIMILARITY_THRESHOLD

    private suspend fun getDetailsAndSave(
        player: TeamPlayer,
        tour: Tour,
        matchPlayer: MatchReportPlayer,
        teamId: TeamId,
    ): PlayerId =
        polishLeagueRepository.getPlayerDetails(tour.season, player.id)
            .map { playerDetails ->
                insert(
                    playerWithDetails = PlayerWithDetails(teamPlayer = player, details = playerDetails),
                    tour = tour,
                    matchPlayer = matchPlayer,
                    teamId = teamId,
                )
            }.value ?: player.id

    private suspend fun insert(
        playerWithDetails: PlayerWithDetails,
        tour: Tour,
        matchPlayer: MatchReportPlayer,
        teamId: TeamId,
    ): PlayerId {
        if (teamId != playerWithDetails.teamPlayer.team) {
            log("Detected new team (${teamId.value}) for the player: ${playerWithDetails.teamPlayer.name} (${playerWithDetails.teamPlayer.id})")
        }
        if (matchPlayer.shirtNumber != playerWithDetails.details.number) {
            log("Detected new shirt number (${matchPlayer.shirtNumber}) for the player: ${playerWithDetails.teamPlayer.name} ${playerWithDetails.teamPlayer.id}")
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
            ).map { it.toPlayer() },
            tourId = tour.id,
        ).onFailure { error -> onInsertFailure(error = error, teamPlayer = playerWithDetails.teamPlayer) }
        return playerWithDetails.teamPlayer.id
    }

    private fun onInsertFailure(error: InsertPlayerError, teamPlayer: TeamPlayer) {
        val message = when (error) {
            is InsertPlayerError.Errors -> buildString {
                append("Player already inserted: $teamPlayer ")
                    .takeIf { error.teamPlayersAlreadyExists.isNotEmpty() }
                append("Team not found: ${error.teamsNotFound.joinToString { it.value.toString() }}")
                    .takeIf { error.teamsNotFound.isNotEmpty() }
            }
            InsertPlayerError.TourNotFound -> "Tour not found"
        }
        log(message)
    }

    private fun log(message: String) {
        Logger.i(message = message, tag = TAG)
    }

    companion object {
        private const val NAME_SIMILARITY_THRESHOLD = 0.7
        private const val TAG = "FixWrongPlayers"
    }
}
