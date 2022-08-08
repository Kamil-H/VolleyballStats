package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.domain.models.Player
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.domain.utils.Logger
import com.kamilh.volleyballstats.domain.utils.Severity
import com.kamilh.volleyballstats.models.MatchReportTeam
import com.kamilh.volleyballstats.models.matchReportPlayerOf
import com.kamilh.volleyballstats.models.matchReportTeamOf
import com.kamilh.volleyballstats.network.result.networkFailureOf
import com.kamilh.volleyballstats.network.result.networkSuccessOf
import com.kamilh.volleyballstats.repository.polishleague.PlsRepository
import com.kamilh.volleyballstats.repository.polishleague.networkErrorOf
import com.kamilh.volleyballstats.repository.polishleague.plsRepositoryOf
import com.kamilh.volleyballstats.storage.InsertPlayerResult
import com.kamilh.volleyballstats.storage.PlayerStorage
import com.kamilh.volleyballstats.storage.playerStorageOf
import com.kamilh.volleyballstats.utils.testAppDispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FixWrongPlayersInteractorTest {

    private fun interactor(
        appDispatchers: AppDispatchers = testAppDispatchers,
        playerStorage: PlayerStorage = playerStorageOf(),
        polishLeagueRepository: PlsRepository = plsRepositoryOf(),
    ): FixWrongPlayersInteractor = FixWrongPlayersInteractor(
        appDispatchers = appDispatchers,
        playerStorage = playerStorage,
        polishLeagueRepository = polishLeagueRepository,
    )

    private fun paramsOf(
        team: MatchReportTeam = matchReportTeamOf(),
        playersNotFound: List<Pair<PlayerId, TeamId>> = emptyList(),
        tour: Tour = tourOf(),
    ): FixWrongPlayersParams = FixWrongPlayersParams(
        team = team,
        playersNotFound = playersNotFound,
        tour = tour,
    )

    @Before
    fun setLogger() {
        Logger.setLogger { severity: Severity, tag: String?, message: String ->
            println("${severity.shorthand}/$tag: $message")
        }
    }

    @Test
    fun `interactor returns the same team when playersNotFound list is empty`() = runTest {
        // GIVEN
        val playersNotFound = emptyList<Pair<PlayerId, TeamId>>()
        val team = matchReportTeamOf()

        // WHEN
        val result = interactor()(
            paramsOf(
                team = team,
                playersNotFound = playersNotFound,
            )
        )

        // THEN
        assert(result == team)
    }

    @Test
    fun `interactor returns the same team when playersNotFound list contains a player that doesn't belong to any of the teams`() =
        runTest {
            // GIVEN
            val playerId = playerIdOf(1)
            val teamId = teamIdOf(1)
            val playersNotFound = listOf(playerId to teamId)
            val team = matchReportTeamOf()

            // WHEN
            val result = interactor()(
                paramsOf(
                    team = team,
                    playersNotFound = playersNotFound,
                )
            )

            // THEN
            assert(result == team)
        }

    @Test
    fun `interactor returns the same team when repository returns Errors`() = runTest {
        // GIVEN
        val playerId = playerIdOf(1)
        val teamId = teamIdOf(1)
        val playersNotFound = listOf(playerId to teamId)
        val team = matchReportTeamOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(
                getAllPlayers = networkFailureOf(networkErrorOf()),
                getAllPlayersByTour = networkFailureOf(networkErrorOf()),
            )
        )(
            paramsOf(
                team = team,
                playersNotFound = playersNotFound,
            )
        )

        // THEN
        assert(result == team)
    }

    @Test
    fun `interactor returns the same team when repository returns empty player lists`() = runTest {
        // GIVEN
        val playerId = playerIdOf(1)
        val teamId = teamIdOf(1)
        val playersNotFound = listOf(playerId to teamId)
        val team = matchReportTeamOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(
                getAllPlayers = networkSuccessOf(emptyList()),
                getAllPlayersByTour = networkSuccessOf(emptyList()),
            )
        )(
            paramsOf(
                team = team,
                playersNotFound = playersNotFound,
            )
        )

        // THEN
        assert(result == team)
    }

    @Test
    fun `interactor returns updated id when player was found by firstName and lastName`() = runTest {
        // GIVEN
        val newPlayerId = playerIdOf(2)
        val playerId = playerIdOf(1)
        val teamId = teamIdOf(1)
        val playersNotFound = listOf(playerId to teamId)
        val firstName = "firstName"
        val lastName = "lastName"
        val name = "$firstName $lastName"
        val matchPlayer = matchReportPlayerOf(
            firstName = firstName,
            lastName = lastName,
            id = playerId,
        )
        val team = matchReportTeamOf(players = listOf(matchPlayer))

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(
                getAllPlayers = networkSuccessOf(
                    listOf(
                        playerSnapshotOf(
                            id = newPlayerId,
                            name = name,
                        )
                    )
                ),
                getAllPlayersByTour = networkSuccessOf(
                    listOf(
                        teamPlayerOf(
                            id = newPlayerId,
                        )
                    )
                ),
            )
        )(
            paramsOf(
                team = team,
                playersNotFound = playersNotFound,
            )
        )

        // THEN
        assert(result.players.all { it.id == newPlayerId })
    }

    @Test
    fun `interactor returns updated id when player was found by name similarity`() = runTest {
        // GIVEN
        val newPlayerId = playerIdOf(2)
        val playerId = playerIdOf(1)
        val teamId = teamIdOf(1)
        val playersNotFound = listOf(playerId to teamId)
        val firstName = "fristName"
        val lastName = "lastName"
        val name = "firstName $lastName"
        val matchPlayer = matchReportPlayerOf(
            firstName = firstName,
            lastName = lastName,
            id = playerId,
        )
        val team = matchReportTeamOf(players = listOf(matchPlayer))

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(
                getAllPlayers = networkSuccessOf(
                    listOf(
                        playerSnapshotOf(
                            id = newPlayerId,
                            name = name,
                        )
                    )
                ),
                getAllPlayersByTour = networkSuccessOf(
                    listOf(teamPlayerOf(id = newPlayerId))
                ),
            )
        )(
            paramsOf(
                team = team,
                playersNotFound = playersNotFound
            )
        )

        // THEN
        assert(result.players.all { it.id == newPlayerId })
    }

    @Test
    fun `interactor returns updated id when player was found id`() = runTest {
        // GIVEN
        val playerId = playerIdOf(1)
        val teamId = teamIdOf(1)
        val playersNotFound = listOf(playerId to teamId)
        val matchPlayer = matchReportPlayerOf(
            id = playerId,
            firstName = "firstName",
            lastName = "lastName"
        )
        val team = matchReportTeamOf(players = listOf(matchPlayer))

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(
                getAllPlayers = networkSuccessOf(
                    listOf(playerSnapshotOf(id = playerId))
                ),
                getAllPlayersByTour = networkSuccessOf(
                    listOf(teamPlayerOf(id = playerId))
                ),
            )
        )(
            paramsOf(
                team = team,
                playersNotFound = playersNotFound
            )
        )

        // THEN
        assert(result.players.all { it.id == playerId })
    }

    @Test
    fun `interactor inserts player with updated team id and shirtNumber`() = runTest {
        // GIVEN
        val playerId = playerIdOf(1)
        val teamId = teamIdOf(1)
        val playersNotFound = listOf(playerId to teamId)
        val shirtNumber = 7
        val matchPlayer = matchReportPlayerOf(id = playerId, shirtNumber = shirtNumber)
        val team = matchReportTeamOf(players = listOf(matchPlayer))
        var insertedPlayers = emptyList<Player>()

        // WHEN
        interactor(
            polishLeagueRepository = plsRepositoryOf(
                getAllPlayers = networkSuccessOf(listOf(playerSnapshotOf(id = playerId))),
                getAllPlayersByTour = networkSuccessOf(listOf(teamPlayerOf(id = playerId))),
            ),
            playerStorage = playerStorageOf(
                insert = { playerWithDetails, _ ->
                    insertedPlayers = playerWithDetails
                    InsertPlayerResult.success(Unit)
                }
            )
        )(
            paramsOf(
                team = team,
                playersNotFound = playersNotFound
            )
        )

        // THEN
        assert(
            insertedPlayers.all {
                it.number == shirtNumber
            } && insertedPlayers.all {
                it.team == teamId
            }
        )
    }

    @Test
    fun `interactor returns MatchTeam without player that coulnd't be found`() = runTest {
        // GIVEN
        val playerId = playerIdOf(1)
        val teamId = teamIdOf(1)
        val playersNotFound = listOf(playerId to teamId)
        val matchPlayer = matchReportPlayerOf(
            id = playerId,
            firstName = "firstName",
            lastName = "lastName",
        )
        val team = matchReportTeamOf(players = listOf(matchPlayer))

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(
                getAllPlayers = networkSuccessOf(listOf(playerSnapshotOf())),
                getAllPlayersByTour = networkSuccessOf(listOf(teamPlayerOf())),
            ),
        )(
            paramsOf(
                team = team,
                playersNotFound = playersNotFound
            )
        )

        // THEN
        assert(result.players.isEmpty())
    }
}

fun wrongPlayerFixerOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: MatchReportTeam = matchReportTeamOf(),
): FixWrongPlayers = object : FixWrongPlayers(appDispatchers) {

    override suspend fun doWork(params: FixWrongPlayersParams): MatchReportTeam = invoke
}