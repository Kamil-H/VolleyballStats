package com.kamilh.storage

import app.cash.turbine.test
import com.kamilh.models.*
import com.kamilh.repository.polishleague.tourYearOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class SqlSqlTourStorageTest : DatabaseTest() {

    private val storage: SqlTourStorage by lazy {
        SqlTourStorage(
            queryRunner = testQueryRunner,
            tourQueries = tourQueries,
            clock = clock,
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
    fun `insert inserts tour when league is in database`() = runBlockingTest {
        // GIVEN
        val league = leagueOf()
        val tour = tourOf(league = league)

        // WHEN
        configure(leagues = listOf(league))
        assert(tourQueries.selectAll().executeAsList().isEmpty())
        val result = storage.insert(tour)

        // THEN
        result.assertSuccess {  }
        assert(tourQueries.selectAll().executeAsList().isNotEmpty())
    }

    @Test
    fun `insert returns LeagueNotFound error when no league is in the database`() = runBlockingTest {
        // GIVEN
        val tour = tourOf()

        // WHEN
        val result = storage.insert(tour)

        // THEN
        result.assertFailure {
            assert(this == InsertTourError.LeagueNotFound)
        }
        assert(tourQueries.selectAll().executeAsList().isEmpty())
    }

    @Test
    fun `insert returns TourAlreadyExists error when no trying to insert a tour with the same TourYear`() = runBlockingTest {
        // GIVEN
        val league = leagueOf()
        val tourYear = tourYearOf()
        val tour = tourOf(year = tourYear)
        val tour1 = tourOf(year = tourYear)

        // WHEN
        configure(leagues = listOf(league))
        val result = storage.insert(tour)
        val result1 = storage.insert(tour1)

        // THEN
        result.assertSuccess {  }
        result1.assertFailure { assert(this == InsertTourError.TourAlreadyExists) }
    }

    @Test
    fun `getAllByLeague returns empty list when no entries in the database`() = runBlockingTest {
        // GIVEN
        val league = leagueOf()

        // WHEN
        configure(leagues = listOf(league))

        // THEN
        storage.getAllByLeague(league).test {
            assert(awaitItem().isEmpty())
        }
    }

    @Test
    fun `getAllByLeague returns not empty list when there are entries in the database`() = runBlockingTest {
        // GIVEN
        val league = leagueOf(division = 1)
        val tour = tourOf(league = league)

        // WHEN
        configure(leagues = listOf(league), tours = listOf(tour))

        // THEN
        storage.getAllByLeague(league).test {
            assert(awaitItem().isNotEmpty())
        }
    }

    @Test
    fun `getByTourYearAndLeague returns empty list when no entries in the database`() = runBlockingTest {
        // GIVEN
        val tourYear = tourYearOf()
        val league = leagueOf(division = 1)

        // WHEN
        configure(leagues = listOf(league))

        // THEN
        storage.getByTourYearAndLeague(tourYear, league).test {
            assert(awaitItem() == null)
        }
    }

    @Test
    fun `updateWinner updates winner_id, end_time and updated_at correctly`() = runBlockingTest {
        // GIVEN
        val tourYear = tourYearOf()
        val league = leagueOf(division = 1)
        val updatedAt = LocalDateTime.now(clock).minusDays(1)
        val tour = tourOf(year = tourYear, league = league, updatedAt = updatedAt)
        val teamId = teamIdOf(1)
        val endTime = LocalDate.now(clock)

        // WHEN
        configure(leagues = listOf(league), tours = listOf(tour))
        storage.update(tour, teamId, endTime)

        // THEN
        val value = tourQueries.selectAll().executeAsList().first()
        assert(value.winner_id == teamId)
        assert(value.end_date == endTime)
        assert(value.updated_at != updatedAt)
    }
}