package com.kamilh.storage

import app.cash.turbine.test
import com.kamilh.models.*
import com.kamilh.repository.polishleague.seasonOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class TeamStorageTest : DatabaseTest() {

    private val storage: SqlTeamStorage by lazy {
        SqlTeamStorage(
            queryRunner = TestQueryRunner(),
            teamQueries = teamQueries,
            tourTeamQueries = tourTeamQueries,
            tourQueries = tourQueries,
        )
    }

    private fun configure(
        leagues: List<League> = emptyList(),
        tours: List<Tour> = emptyList(),
        insertTeams: List<InsertTeam> = emptyList(),
    ) {
        leagues.forEach { insert(it) }
        tours.forEach { insert(it) }
        insertTeams.forEach { insert(it) }
    }

    @Test
    fun `TourNotFound error returned when no tour in database`() = runBlockingTest {
        // GIVEN
        val team = teamOf()
        val tourId = tourIdOf()

        // WHEN
        val result = storage.insert(listOf(team), tourId)

        // THEN
        result.assertFailure {
            assert(this == InsertTeamError.TourNotFound)
        }
    }

    @Test
    fun `Success is returned when there are no teams to insert`() = runBlockingTest {
        // GIVEN
        val tourId = tourIdOf()

        // WHEN
        val result = storage.insert(emptyList(), tourId)

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `Success is returned when there are teams to insert and tour and league exists`() = runBlockingTest {
        // GIVEN
        val season = seasonOf()
        val tour = tourOf(season = season)
        val league = leagueOf()
        configure(leagues = listOf(league), tours = listOf(tour))

        // WHEN
        val result = storage.insert(emptyList(), tour.id)

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `TourTeamAlreadyExists with correct teamId is returned even though it happens as first`() = runBlockingTest {
        // GIVEN
        val tourYear = seasonOf()
        val tour = tourOf(season = tourYear)
        val league = leagueOf()
        val teamId = teamIdOf(value = 1)
        val team = teamOf(id = teamId)
        configure(leagues = listOf(league), tours = listOf(tour), insertTeams = listOf(InsertTeam(team, tour)))

        // WHEN
        val result = storage.insert(listOf(team), tour.id)

        // THEN
        result.assertFailure {
            require(this is InsertTeamError.TourTeamAlreadyExists)
            assert(teamIds.contains(teamId))
        }
    }

    @Test
    fun `TourTeamAlreadyExists with correct teamId is returned when this not happens as first`() = runBlockingTest {
        // GIVEN
        val tourYear = seasonOf()
        val tour = tourOf(season = tourYear)
        val league = leagueOf()
        val teamId = teamIdOf(value = 1)
        val team = teamOf(id = teamId)
        val secondTeam = teamOf()
        configure(leagues = listOf(league), tours = listOf(tour), insertTeams = listOf(InsertTeam(team, tour)))

        // WHEN
        val result = storage.insert(listOf(secondTeam, team), tour.id)

        // THEN
        result.assertFailure {
            require(this is InsertTeamError.TourTeamAlreadyExists)
            assert(teamIds.contains(teamId))
        }
    }

    @Test
    fun `TourTeamAlreadyExists with correct teamId is returned when trying to add the same team multiple times`() = runBlockingTest {
        // GIVEN
        val tourYear = seasonOf()
        val tour = tourOf(season = tourYear)
        val league = leagueOf()
        val teamId = teamIdOf(value = 1)
        val team = teamOf(id = teamId)
        configure(leagues = listOf(league), tours = listOf(tour))

        // WHEN
        val result = storage.insert(listOf(team, team), tour.id)

        // THEN
        result.assertFailure {
            require(this is InsertTeamError.TourTeamAlreadyExists)
            assert(teamIds.contains(teamId))
        }
    }

    @Test
    fun `getAllTeams returns empty list when no entries in the database`() = runBlockingTest {
        // GIVEN
        val league = leagueOf()
        val tourId = tourIdOf()

        // WHEN
        configure(leagues = listOf(league))

        // THEN
        storage.getAllTeams(tourId).test {
            assert(awaitItem().isEmpty())
        }
    }

    @Test
    fun `getAllByLeague returns not empty list when there are entries in the database`() = runBlockingTest {
        // GIVEN
        val league = leagueOf(division = 1)
        val tourYear = seasonOf()
        val tour = tourOf(season = tourYear, league = league)
        val team = teamOf()

        // WHEN
        configure(leagues = listOf(league), tours = listOf(tour), insertTeams = listOf(InsertTeam(team, tour)))

        // THEN
        storage.getAllTeams(tour.id).test {
            assert(awaitItem().isNotEmpty())
        }
    }

    @Test
    fun `getTeam returns not null value when there is team with provided name`() = runBlockingTest {
        // GIVEN
        val league = leagueOf(division = 1)
        val tourYear = seasonOf()
        val tour = tourOf(season = tourYear, league = league)
        val name = "name"
        val team = teamOf(name = name)
        val code = "code"

        // WHEN
        configure(leagues = listOf(league), tours = listOf(tour), insertTeams = listOf(InsertTeam(team, tour)))
        val insertedTeam = storage.getTeam(name, code, tour.id)

        // THEN
        assert(insertedTeam == team)
    }

    @Test
    fun `getTeam returns null value when there is no team with provided name`() = runBlockingTest {
        // GIVEN
        val league = leagueOf(division = 1)
        val tourYear = seasonOf()
        val tour = tourOf(season = tourYear, league = league)
        val name = "name"
        val team = teamOf(name = name)
        val code = "code"

        // WHEN
        configure(leagues = listOf(league), tours = listOf(tour), insertTeams = listOf(InsertTeam(team, tour)))
        val insertedTeam = storage.getTeam("some name", code, tour.id)

        // THEN
        assert(insertedTeam == null)
    }

    @Test
    fun `getTeam returns not null value when there is no team with provided name, but there is with provided code`() = runBlockingTest {
        // GIVEN
        val league = leagueOf(division = 1)
        val tourYear = seasonOf()
        val tour = tourOf(season = tourYear, league = league)
        val name = "name"
        val team = teamOf(name = name)
        val code = "code"

        // WHEN
        teamQueries.updateCode(code, team.id)
        configure(leagues = listOf(league), tours = listOf(tour), insertTeams = listOf(InsertTeam(team, tour)))
        val insertedTeam = storage.getTeam("some name", code, tour.id)

        // THEN
        assert(insertedTeam == null)
    }

    @Test
    fun `getTeam returns null value when no entries in the database`() = runBlockingTest {
        // GIVEN
        val league = leagueOf(division = 1)
        val tourYear = seasonOf()
        val tour = tourOf(season = tourYear, league = league)
        val name = "name"
        val code = "code"

        // WHEN
        configure(leagues = listOf(league), tours = listOf(tour))
        val insertedTeam = storage.getTeam(name, code, tour.id)

        // THEN
        assert(insertedTeam == null)
    }
}

fun teamStorageOf(
    insert: InsertTeamResult = Result.success(Unit),
    getAllTeams: Flow<List<Team>> = flowOf(emptyList()),
    getTeam: List<Team> = emptyList(),
): TeamStorage =
    object : TeamStorage {
        override suspend fun insert(teams: List<Team>, tourId: TourId): InsertTeamResult = insert
        override suspend fun getAllTeams(tourId: TourId): Flow<List<Team>> = getAllTeams
        override suspend fun getTeam(name: String, code: String, tourId: TourId): Team? = getTeam.firstOrNull { it.name == name }
    }