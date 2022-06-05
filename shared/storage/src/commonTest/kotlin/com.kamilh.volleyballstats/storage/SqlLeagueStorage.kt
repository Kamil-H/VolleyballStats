package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.assertFailure
import com.kamilh.volleyballstats.domain.assertSuccess
import com.kamilh.volleyballstats.domain.countryOf
import com.kamilh.volleyballstats.domain.leagueOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        assertTrue(leagueQueries.selectAll().executeAsList().isEmpty())
        val result = storage.insert(league)

        // THEN
        result.assertSuccess()
        assertTrue(leagueQueries.selectAll().executeAsList().isNotEmpty())
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
            assertEquals(expected = InsertLeagueError.LeagueAlreadyExists, this)
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