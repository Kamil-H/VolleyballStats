package com.kamilh.volleyballstats.storage

import app.cash.turbine.test
import com.kamilh.volleyballstats.models.*
import com.kamilh.volleyballstats.repository.polishleague.seasonOf
import com.kamilh.volleyballstats.utils.localDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import com.kamilh.volleyballstats.models.PlayerWithDetails
import org.junit.Test
import com.kamilh.volleyballstats.storage.testQueryRunner
import kotlin.time.Duration.Companion.days

class SqlPlayerStorageTest : DatabaseTest() {

    private val storage: SqlPlayerStorage by lazy {
        SqlPlayerStorage(
            queryRunner = testQueryRunner,
            playerQueries = playerQueries,
            teamPlayerQueries = teamPlayerQueries,
            tourTeamQueries = tourTeamQueries,
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
    fun `insert returns TourNotFound when no tour in the database`() = runTest {
        // GIVEN
        val player = playerWithDetailsOf()
        val tourId = tourIdOf()

        // WHEN
        val result = storage.insert(listOf(player), tourId)

        // THEN
        result.assertFailure()
    }

    @Test
    fun `insert returns Success when no players to add`() = runTest {
        // GIVEN
        val tourId = tourIdOf()

        // WHEN
        val result = storage.insert(emptyList(), tourId)

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `insert returns Success when correct team and tour is in the database`() = runTest {
        // GIVEN
        val tour = tourOf()
        val teamId = teamIdOf(1)
        val team = teamOf(id = teamId)
        val player = playerWithDetailsOf(
            teamPlayer = teamPlayerOf(team = teamId)
        )
        configure(
            leagues = listOf(tour.league),
            tours = listOf(tour),
            teams = listOf(InsertTeam(team, tour)),
        )

        // WHEN
        val result = storage.insert(listOf(player), tour.id)

        // THEN
        result.assertSuccess()
        playerQueries.selectAll().executeAsList().isNotEmpty()
        teamPlayerQueries.selectAll().executeAsList().isNotEmpty()
    }

    @Test
    fun `insert returns Errors when team is not in the database`() = runTest {
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
        val result = storage.insert(listOf(player), tour.id)

        // THEN
        result.assertFailure {
            require(this is InsertPlayerError.Errors)
            assert(this.teamsNotFound.contains(teamId))
        }
        playerQueries.selectAll().executeAsList().isEmpty()
        teamPlayerQueries.selectAll().executeAsList().isEmpty()
    }

    @Test
    fun `insert returns Errors when player is already in the database`() = runTest {
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
            teams = listOf(InsertTeam(team, tour)),
            players = listOf(InsertPlayer(player, tour))
        )

        // WHEN
        val result = storage.insert(listOf(player), tour.id)

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
    fun `getAllPlayers returns a correct data`() = runTest {
        // GIVEN
        val league = leagueOf()
        val tourYear = seasonOf()
        val tour = tourOf(season = tourYear)
        val teamId = teamIdOf(1)
        val team = teamOf(id = teamId)
        val now = localDateTime()
        val playerUpdatedAt = now.plus(1.days)
        val detailsUpdatedAt = now.plus(2.days)
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
            teams = listOf(InsertTeam(team, tour)),
            players = listOf(InsertPlayer(teamPlayer, tour))
        )

        // WHEN
        val result = storage.getAllPlayers(teamId, tour.id)

        // THEN
        result.test {
            awaitItem().contains(teamPlayer)
        }
    }
}

fun playerStorageOf(
    insert: (players: List<PlayerWithDetails>, tourId: TourId) -> InsertPlayerResult = { _, _ ->
        InsertPlayerResult.success(Unit)
    },
    getAllPlayersByTeam: Flow<List<PlayerWithDetails>> = flowOf(emptyList()),
    getAllPlayers: Flow<List<PlayerWithDetails>> = flowOf(emptyList()),
): PlayerStorage = object : PlayerStorage {
    override suspend fun insert(players: List<PlayerWithDetails>, tourId: TourId): InsertPlayerResult =
        insert(players, tourId)
    override fun getAllPlayers(teamId: TeamId, tourId: TourId): Flow<List<PlayerWithDetails>> = getAllPlayersByTeam
    override fun getAllPlayers(tourId: TourId): Flow<List<PlayerWithDetails>> = getAllPlayers
}