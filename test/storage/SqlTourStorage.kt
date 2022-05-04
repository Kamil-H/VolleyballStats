package com.kamilh.storage

import app.cash.turbine.test
import com.kamilh.datetime.LocalDate
import com.kamilh.models.*
import com.kamilh.repository.polishleague.seasonOf
import com.kamilh.utils.localDate
import com.kamilh.utils.localDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import storage.testQueryRunner
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
        assert(tourQueries.selectAll().executeAsList().isEmpty())
        val result = storage.insert(tour)

        // THEN
        result.assertSuccess {  }
        assert(tourQueries.selectAll().executeAsList().isNotEmpty())
    }

    @Test
    fun `insert returns LeagueNotFound error when no league is in the database`() = runTest {
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
        result1.assertFailure { assert(this == InsertTourError.TourAlreadyExists) }
    }

    @Test
    fun `getAllByLeague returns empty list when no entries in the database`() = runTest {
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
    fun `getAllByLeague returns not empty list when there are entries in the database`() = runTest {
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
    fun `getByTourYearAndLeague returns empty list when no entries in the database`() = runTest {
        // GIVEN
        val tourId = tourIdOf()
        val league = leagueOf(division = 1)

        // WHEN
        configure(leagues = listOf(league))

        // THEN
        storage.getByTourId(tourId).test {
            assert(awaitItem() == null)
        }
    }

    @Test
    fun `updateWinner updates winner_id, end_time and updated_at correctly`() = runTest {
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
        assert(value.end_date == endTime)
        assert(value.updated_at != updatedAt)
    }
}

fun tourStorageOf(
    insert: (tour: Tour) -> InsertTourResult = { InsertTourResult.success(Unit) },
    getAll: Flow<List<Tour>> = flowOf(emptyList()),
    getAllByLeague: Flow<List<Tour>> = flowOf(emptyList()),
    getByTourId: Flow<Tour?> = flowOf(null),
    onUpdate: (tour: Tour, endTime: LocalDate) -> Unit = { _, _ -> },
): TourStorage = object : TourStorage {
    override suspend fun insert(tour: Tour): InsertTourResult = insert(tour)
    override fun getAll(): Flow<List<Tour>> = getAll
    override suspend fun getAllByLeague(league: League): Flow<List<Tour>> = getAllByLeague
    override suspend fun getByTourId(tourId: TourId): Flow<Tour?> = getByTourId
    override suspend fun update(tour: Tour, endTime: LocalDate) = onUpdate(tour, endTime)
}