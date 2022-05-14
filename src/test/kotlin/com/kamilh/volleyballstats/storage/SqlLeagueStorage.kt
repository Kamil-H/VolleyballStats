package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.models.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import com.kamilh.volleyballstats.storage.testQueryRunner

class SqlLeagueStorageTest : DatabaseTest() {

    private val storage: SqlLeagueStorage by lazy {
        SqlLeagueStorage(
            queryRunner = testQueryRunner,
            leagueQueries = leagueQueries,
        )
    }

    @Test
    fun `insert works correctly`() = runTest {
        // GIVEN
        val league = leagueOf()

        // WHEN
        assert(leagueQueries.selectAll().executeAsList().isEmpty())
        val result = storage.insert(league)

        // THEN
        result.assertSuccess()
        assert(leagueQueries.selectAll().executeAsList().isNotEmpty())
    }

    @Test
    fun `it's not possible to insert two same leagues`() = runTest {
        // GIVEN
        val league = leagueOf()

        // WHEN
        val result1 = storage.insert(league)
        val result2 = storage.insert(league)

        // THEN
        result1.assertSuccess()
        result2.assertFailure {
            assert(this == InsertLeagueError.LeagueAlreadyExists)
        }
    }

    @Test
    fun `it's possible to insert two leagues with different division`() = runTest {
        // GIVEN
        val league = leagueOf(division = 0)
        val league1 = leagueOf(division = 1)

        // WHEN
        val result1 = storage.insert(league)
        val result2 = storage.insert(league1)

        // THEN
        result1.assertSuccess()
        result2.assertSuccess()
    }

    @Test
    fun `it's possible to insert two leagues with different country`() = runTest {
        // GIVEN
        val league = leagueOf(country = countryOf("PL"))
        val league1 = leagueOf(country = countryOf("US"))

        // WHEN
        val result1 = storage.insert(league)
        val result2 = storage.insert(league1)

        // THEN
        result1.assertSuccess()
        result2.assertSuccess()
    }
}

fun leagueStorageOf(
    insert: InsertLeagueResult = InsertLeagueResult.success(Unit)
): LeagueStorage = object : LeagueStorage {
    override suspend fun insert(league: League): InsertLeagueResult = insert
}