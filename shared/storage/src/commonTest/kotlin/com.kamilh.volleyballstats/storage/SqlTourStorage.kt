package com.kamilh.volleyballstats.storage

import app.cash.turbine.test
import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.assertFailure
import com.kamilh.volleyballstats.domain.assertSuccess
import com.kamilh.volleyballstats.utils.localDate
import com.kamilh.volleyballstats.utils.localDateTime
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import kotlin.time.Duration.Companion.days

class SqlSqlTourStorageTest : DatabaseTest() {

    private val storage: SqlTourStorage by lazy {
        SqlTourStorage(
            queryRunner = testQueryRunner,
            tourQueries = tourQueries,
        )
    }

    private fun configure(
        leagues: List<League> = emptyList(),
        tours: List<Tour> = emptyList(),
    ) {
        leagues.forEach { insert(it) }
        tours.forEach { insert(it) }
    }

    @Test
    fun `insert inserts tour when league is in database`() = runTest {
        // GIVEN
        val league = leagueOf()
        val tour = tourOf(league = league)

        // WHEN
        configure(leagues = listOf(league))
        assertTrue(tourQueries.selectAll().executeAsList().isEmpty())
        val result = storage.insert(tour)

        // THEN
        result.assertSuccess {  }
        assertTrue(tourQueries.selectAll().executeAsList().isNotEmpty())
    }

    @Test
    fun `insert returns LeagueNotFound error when no league is in the database`() = runTest {
        // GIVEN
        val tour = tourOf()

        // WHEN
        val result = storage.insert(tour)

        // THEN
        result.assertFailure {
            assertEquals(expected = InsertTourError.LeagueNotFound, this)
        }
        assertTrue(tourQueries.selectAll().executeAsList().isEmpty())
    }

    @Test
    fun `insert returns TourAlreadyExists error when no trying to insert a tour with the same TourYear`() = runTest {
        // GIVEN
        val league = leagueOf()
        val tourYear = seasonOf()
        val tour = tourOf(season = tourYear)
        val tour1 = tourOf(season = tourYear)

        // WHEN
        configure(leagues = listOf(league))
        val result = storage.insert(tour)
        val result1 = storage.insert(tour1)

        // THEN
        result.assertSuccess {  }
        result1.assertFailure {
            assertEquals(expected = InsertTourError.TourAlreadyExists, this)
        }
    }

    @Test
    fun `getAllByLeague returns empty list when no entries in the database`() = runTest {
        // GIVEN
        val league = leagueOf()

        // WHEN
        configure(leagues = listOf(league))

        // THEN
        storage.getAllByLeague(league).test {
            assertTrue(awaitItem().isEmpty())
        }
    }

    @Test
    fun `getAllByLeague returns not empty list when there are entries in the database`() = runTest {
        // GIVEN
        val league = leagueOf(division = 1)
        val tour = tourOf(league = league)

        // WHEN
        configure(leagues = listOf(league), tours = listOf(tour))

        // THEN
        storage.getAllByLeague(league).test {
            assertTrue(awaitItem().isNotEmpty())
        }
    }

    @Test
    fun `getByTourYearAndLeague returns empty list when no entries in the database`() = runTest {
        // GIVEN
        val tourId = tourIdOf()
        val league = leagueOf(division = 1)

        // WHEN
        configure(leagues = listOf(league))

        // THEN
        storage.getByTourId(tourId).test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `updateWinner updates end_time and updated_at correctly`() = runTest {
        // GIVEN
        val tourYear = seasonOf()
        val league = leagueOf(division = 1)
        val updatedAt = localDateTime().minus(1.days)
        val tour = tourOf(season = tourYear, league = league, updatedAt = updatedAt)
        val endTime = localDate()

        // WHEN
        configure(leagues = listOf(league), tours = listOf(tour))
        storage.update(tour, endTime)

        // THEN
        val value = tourQueries.selectAll().executeAsList().first()
        assertEquals(expected = endTime, value.end_date)
        assertNotEquals(illegal = updatedAt, value.updated_at)
    }
}