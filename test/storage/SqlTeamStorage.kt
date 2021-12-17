package com.kamilh.storage

import com.kamilh.models.Team
import com.kamilh.models.Tour
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
    fun `test `() = runBlockingTest {
        // GIVEN


        // WHEN


        // THEN

    }
}

fun teamStorageOf(
    getAllTeams: List<Team> = emptyList(),
    getTeam: List<Team> = emptyList(),
): TeamStorage =
    object : TeamStorage {
        override suspend fun insert(tour: Tour, teams: List<Team>) {  }
        override suspend fun getAllTeams(tour: Tour): List<Team> = getAllTeams
        override suspend fun getTeam(name: String, tour: Tour): Team? = getTeam.firstOrNull { it.name == name }
    }