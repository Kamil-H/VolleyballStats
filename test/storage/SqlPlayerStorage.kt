package com.kamilh.storage

import app.cash.turbine.test
import com.kamilh.models.*
import com.kamilh.repository.polishleague.seasonOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import models.PlayerWithDetails
import org.junit.Test
import java.time.LocalDateTime

class SqlPlayerStorageTest : DatabaseTest() {

    private val storage: SqlPlayerStorage by lazy {
        SqlPlayerStorage(
            queryRunner = testQueryRunner,
            playerQueries = playerQueries,
            teamPlayerQueries = teamPlayerQueries,
            tourTeamQueries = tourTeamQueries,
            tourQueries = tourQueries,
        )
    }

    private fun configure(
        leagues: List<League> = emptyList(),
        tours: List<Tour> = emptyList(),
        teams: List<InsertTeam> = emptyList(),
        players: List<InsertPlayer> = emptyList(),
    ) {
        leagues.forEach { insert(it) }
        tours.forEach { insert(it) }
        teams.forEach { insert(it) }
        players.forEach { insert(it) }
    }

    @Test
    fun `insert returns TourNotFound when no tour in the database`() = runBlockingTest {
        // GIVEN
        val player = playerWithDetailsOf()
        val league = leagueOf()
        val tourYear = seasonOf()

        // WHEN
        val result = storage.insert(listOf(player), league, tourYear)

        // THEN
        result.assertFailure()
    }

    @Test
    fun `insert returns Success when no players to add`() = runBlockingTest {
        // GIVEN
        val league = leagueOf()
        val tourYear = seasonOf()

        // WHEN
        val result = storage.insert(emptyList(), league, tourYear)

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `insert returns Success when correct team and tour is in the database`() = runBlockingTest {
        // GIVEN
        val league = leagueOf()
        val tourYear = seasonOf()
        val tour = tourOf(season = tourYear)
        val teamId = teamIdOf(1)
        val team = teamOf(id = teamId)
        val player = playerWithDetailsOf(
            teamPlayer = teamPlayerOf(team = teamId)
        )
        configure(
            leagues = listOf(league),
            tours = listOf(tour),
            teams = listOf(InsertTeam(team, league, tourYear)),
        )

        // WHEN
        val result = storage.insert(listOf(player), league, tourYear)

        // THEN
        result.assertSuccess()
        playerQueries.selectAll().executeAsList().isNotEmpty()
        teamPlayerQueries.selectAll().executeAsList().isNotEmpty()
    }

    @Test
    fun `insert returns Errors when team is not in the database`() = runBlockingTest {
        // GIVEN
        val league = leagueOf()
        val tourYear = seasonOf()
        val tour = tourOf(season = tourYear, league = league)
        val teamId = teamIdOf(1)
        val player = playerWithDetailsOf(
            teamPlayer = teamPlayerOf(team = teamId)
        )
        configure(
            leagues = listOf(league),
            tours = listOf(tour),
        )

        // WHEN
        val result = storage.insert(listOf(player), league, tourYear)

        // THEN
        result.assertFailure {
            require(this is InsertPlayerError.Errors)
            assert(this.teamsNotFound.contains(teamId))
        }
        playerQueries.selectAll().executeAsList().isEmpty()
        teamPlayerQueries.selectAll().executeAsList().isEmpty()
    }

    @Test
    fun `insert returns Errors when player is already in the database`() = runBlockingTest {
        // GIVEN
        val league = leagueOf()
        val tourYear = seasonOf()
        val tour = tourOf(season = tourYear)
        val teamId = teamIdOf(1)
        val team = teamOf(id = teamId)
        val player = playerWithDetailsOf(
            teamPlayer = teamPlayerOf(team = teamId)
        )
        configure(
            leagues = listOf(league),
            tours = listOf(tour),
            teams = listOf(InsertTeam(team, league, tourYear)),
            players = listOf(InsertPlayer(player, league, tourYear))
        )

        // WHEN
        val result = storage.insert(listOf(player), league, tourYear)

        // THEN
        result.assertFailure {
            require(this is InsertPlayerError.Errors)
            assert(this.teamsNotFound.isEmpty())
            assert(this.teamPlayersAlreadyExists.contains(player.teamPlayer.id))
        }
        playerQueries.selectAll().executeAsList().isEmpty()
        teamPlayerQueries.selectAll().executeAsList().isEmpty()
    }

    @Test
    fun `getAllPlayers returns a correct data`() = runBlockingTest {
        // GIVEN
        val league = leagueOf()
        val tourYear = seasonOf()
        val tour = tourOf(season = tourYear)
        val teamId = teamIdOf(1)
        val team = teamOf(id = teamId)
        val now = LocalDateTime.now()
        val playerUpdatedAt = now.plusDays(1)
        val detailsUpdatedAt = now.plusDays(2)
        val teamPlayer = playerWithDetailsOf(
            teamPlayer = teamPlayerOf(
                team = teamId,
                name = "name",
                specialization = TeamPlayer.Specialization.MiddleBlocker,
                updatedAt = playerUpdatedAt,
            ),
            details = playerDetailsOf(
                height = 201,
                weight = 91,
                updatedAt = detailsUpdatedAt,
            )
        )
        configure(
            leagues = listOf(league),
            tours = listOf(tour),
            teams = listOf(InsertTeam(team, league, tourYear)),
            players = listOf(InsertPlayer(teamPlayer, league, tourYear))
        )

        // WHEN
        val result = storage.getAllPlayers(teamId, league, tourYear)

        // THEN
        result.test {
            awaitItem().contains(teamPlayer)
        }
    }
}

fun playerStorageOf(
    insert: (players: List<PlayerWithDetails>, league: League, tour: Season) -> InsertPlayerResult = { _, _, _ ->
        InsertPlayerResult.success(Unit)
    },
    getAllPlayersByTeam: Flow<List<PlayerWithDetails>> = flowOf(emptyList()),
    getAllPlayers: Flow<List<PlayerWithDetails>> = flowOf(emptyList()),
): PlayerStorage = object : PlayerStorage {
    override suspend fun insert(players: List<PlayerWithDetails>, league: League, season: Season): InsertPlayerResult =
        insert(players, league, season)
    override suspend fun getAllPlayers(teamId: TeamId, league: League, season: Season): Flow<List<PlayerWithDetails>> = getAllPlayersByTeam
    override suspend fun getAllPlayers(league: League, season: Season): Flow<List<PlayerWithDetails>> = getAllPlayers
}