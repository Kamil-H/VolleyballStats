package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.models.*
import com.kamilh.volleyballstats.repository.polishleague.PolishLeagueRepository
import com.kamilh.volleyballstats.storage.InsertPlayerError
import com.kamilh.volleyballstats.storage.PlayerStorage
import com.kamilh.volleyballstats.utils.Logger
import com.kamilh.volleyballstats.utils.findSimilarity
import me.tatarka.inject.annotations.Inject
import com.kamilh.volleyballstats.models.PlayerWithDetails

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
        allPlayers: List<Player>,
        allTeamPlayers: List<TeamPlayer>,
        playersNotFound: List<Pair<PlayerId, TeamId>>,
        tour: Tour,
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
                            tour = tour,
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
        tour: Tour,
        teamId: TeamId,
    ): PlayerId {
        val playerIdFromName = allPlayers.findByName(matchPlayer)
        val player = allTeamPlayers.firstOrNull { it.id == matchPlayer.id } ?: allTeamPlayers.firstOrNull { it.id == playerIdFromName }
        Logger.i("playerIdFromName: $playerIdFromName, player: $player")
        return when {
            player != null -> getDetailsAndSave(player, tour, matchPlayer, teamId)
            else -> when (val playerWithDetails = polishLeagueRepository.getPlayerWithDetails(tour.season, playerIdFromName).value) {
                null -> matchPlayer.id
                else -> insert(playerWithDetails, tour, matchPlayer, teamId)
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
            tourId = tour.id,
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