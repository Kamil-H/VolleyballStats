package com.kamilh.storage

import com.kamilh.models.*
import com.kamilh.repository.polishleague.tourYearOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class TeamStorageTest : DatabaseTest() {

    private val storage: SqlTeamStorage by lazy {
        SqlTeamStorage(
            queryRunner = TestQueryRunner(),
            teamQueries = teamQueries,
            tourTeamQueries = tourTeamQueries,
        )
    }

    @Test
    fun `TourNotFound error returned when no tour in database`() = runBlockingTest {
        // GIVEN
        val team = teamOf()
        val tour = tourYearOf()

        // WHEN
        val result = storage.insert(tour, listOf(team))

        // THEN
        result.assertFailure {
            assert(this == InsertTeamError.TourNotFound)
        }
    }

    @Test
    fun `Success is returned when tour is in the database`() = runBlockingTest {
        // GIVEN
        val team = teamOf()
        val tour = tourYearOf(tour = 2020)

        // WHEN
        val result = storage.insert(tour, listOf(team))

        // THEN
        result.assertFailure {
            assert(this == InsertTeamError.TourNotFound)
        }
    }
}

fun teamStorageOf(
    insert: InsertTeamResult = Result.success(Unit),
    getAllTeams: List<Team> = emptyList(),
    getTeam: List<Team> = emptyList(),
): TeamStorage =
    object : TeamStorage {
        override suspend fun insert(tour: TourYear, teams: List<Team>): InsertTeamResult = insert
        override suspend fun getAllTeams(tour: TourYear): List<Team> = getAllTeams
        override suspend fun getTeam(name: String, tour: TourYear): Team? = getTeam.firstOrNull { it.name == name }
    }