package com.kamilh.storage

import com.kamilh.models.assertFailure
import com.kamilh.models.assertSuccess
import com.kamilh.models.countryOf
import com.kamilh.models.leagueOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class SqlLeagueStorageTest : DatabaseTest() {

    private val storage: SqlLeagueStorage by lazy {
        SqlLeagueStorage(
            queryRunner = TestQueryRunner(),
            leagueQueries = leagueQueries,
        )
    }

    @Test
    fun `insert works correctly`() = runBlockingTest {
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
    fun `it's not possible to insert two same leagues`() = runBlockingTest {
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
    fun `it's possible to insert two leagues with different division`() = runBlockingTest {
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
    fun `it's possible to insert two leagues with different country`() = runBlockingTest {
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