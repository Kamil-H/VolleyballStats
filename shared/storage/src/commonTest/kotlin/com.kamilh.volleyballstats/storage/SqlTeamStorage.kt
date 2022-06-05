package com.kamilh.volleyballstats.storage

import app.cash.turbine.test
import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.assertFailure
import com.kamilh.volleyballstats.domain.assertSuccess
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TeamStorageTest : DatabaseTest() {

    private val storage: SqlTeamStorage by lazy {
        SqlTeamStorage(
            queryRunner = testQueryRunner,
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
    fun `TourNotFound error returned when no tour in database`() = runTest {
        // GIVEN
        val team = teamOf()
        val tourId = tourIdOf()

        // WHEN
        val result = storage.insert(listOf(team), tourId)

        // THEN
        result.assertFailure {
            assertEquals(expected = InsertTeamError.TourNotFound, this)
        }
    }

    @Test
    fun `Success is returned when there are no teams to insert`() = runTest {
        // GIVEN
        val tourId = tourIdOf()

        // WHEN
        val result = storage.insert(emptyList(), tourId)

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `Success is returned when there are teams to insert and tour and league exists`() = runTest {
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
    fun `TourTeamAlreadyExists with correct teamId is returned even though it happens as first`() = runTest {
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
            assertTrue(teamIds.contains(teamId))
        }
    }

    @Test
    fun `TourTeamAlreadyExists with correct teamId is returned when this not happens as first`() = runTest {
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
            assertTrue(teamIds.contains(teamId))
        }
    }

    @Test
    fun `TourTeamAlreadyExists with correct teamId is returned when trying to add the same team multiple times`() = runTest {
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
            assertTrue(teamIds.contains(teamId))
        }
    }

    @Test
    fun `getAllTeams returns empty list when no entries in the database`() = runTest {
        // GIVEN
        val league = leagueOf()
        val tourId = tourIdOf()

        // WHEN
        configure(leagues = listOf(league))

        // THEN
        storage.getAllTeams(tourId).test {
            assertTrue(awaitItem().isEmpty())
        }
    }

    @Test
    fun `getAllByLeague returns not empty list when there are entries in the database`() = runTest {
        // GIVEN
        val league = leagueOf(division = 1)
        val tourYear = seasonOf()
        val tour = tourOf(season = tourYear, league = league)
        val team = teamOf()

        // WHEN
        configure(leagues = listOf(league), tours = listOf(tour), insertTeams = listOf(InsertTeam(team, tour)))

        // THEN
        storage.getAllTeams(tour.id).test {
            assertTrue(awaitItem().isNotEmpty())
        }
    }

    @Test
    fun `getTeam returns not null value when there is team with provided name`() = runTest {
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
        assertEquals(expected =  team, insertedTeam)
    }

    @Test
    fun `getTeam returns null value when there is no team with provided name`() = runTest {
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
        assertNull(insertedTeam)
    }

    @Test
    fun `getTeam returns not null value when there is no team with provided name but there is with provided code`() = runTest {
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
        assertNull(insertedTeam)
    }

    @Test
    fun `getTeam returns null value when no entries in the database`() = runTest {
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
        assertNull(insertedTeam)
    }
}