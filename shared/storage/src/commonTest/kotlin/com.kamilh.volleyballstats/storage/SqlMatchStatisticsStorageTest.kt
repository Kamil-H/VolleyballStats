package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.domain.assertFailure
import com.kamilh.volleyballstats.domain.assertSuccess
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SqlMatchStatisticsStorageTest : StatisticsStorageTest() {

    @Test
    fun `insert and select works properly`() = runTest {
        load()
        yield()
    }

    @Test
    fun `insert returns TourNotFound when there is no such tour`() = runTest {
        // GIVEN
        val tourId = tourIdOf()
        val matchStatistics = matchStatisticsOf()

        // WHEN
        val result = storage.insert(matchStatistics, tourId)

        // THEN
        result.assertFailure {
            assertEquals(expected = InsertMatchStatisticsError.TourNotFound, this)
        }
    }

    @Test
    fun `insert returns TeamNotFound when there is no such team`() = runTest {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = tourOf(league = league, season = season)
        val teamId = teamIdOf(1)
        val matchStatistics = matchStatisticsOf(home = matchTeamOf(teamId = teamId))
        insert(league)
        insert(tour)

        // WHEN
        val result = storage.insert(matchStatistics, tour.id)

        // THEN
        result.assertFailure {
            require(this is InsertMatchStatisticsError.TeamNotFound)
            assertEquals(expected = teamId, this.teamId)
        }
    }

    @Test
    fun `insert updates Home's team code`() = runTest {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = tourOf(league = league, season = season)
        val homeTeamId = teamIdOf(1)
        val awayTeamId = teamIdOf(2)
        val code = "code"
        val matchStatistics = matchStatisticsOf(
            home = matchTeamOf(
                teamId = homeTeamId,
                code = code,
            ),
            away = matchTeamOf(teamId = awayTeamId)
        )
        insert(league)
        insert(tour)
        insert(
            InsertTeam(
                team = teamOf(id = homeTeamId),
                tour = tour,
            )
        )

        // WHEN
        val result = storage.insert(matchStatistics, tour.id)

        // THEN
        assertEquals(expected = code, teamQueries.selectAll().executeAsList().first { it.id == homeTeamId }.code)
        result.assertFailure {
            require(this is InsertMatchStatisticsError.TeamNotFound)
            assertEquals(expected = awayTeamId, this.teamId)
        }
    }

    @Test
    fun `insert updates Away's team code`() = runTest {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = tourOf(league = league, season = season)
        val homeTeamId = teamIdOf(1)
        val awayTeamId = teamIdOf(2)
        val code = "code"
        val matchStatistics = matchStatisticsOf(
            home = matchTeamOf(
                teamId = homeTeamId,
            ),
            away = matchTeamOf(
                teamId = awayTeamId,
                code = code,
            )
        )
        insert(league)
        insert(tour)
        insert(
            InsertTeam(
                team = teamOf(id = homeTeamId),
                tour = tour,
            ),
            InsertTeam(
                team = teamOf(id = awayTeamId),
                tour = tour,
            )
        )

        // WHEN
        storage.insert(matchStatistics, tour.id)

        // THEN
        assertEquals(expected = code, teamQueries.selectAll().executeAsList().first { it.id == awayTeamId }.code)
    }

    @Test
    fun `insert returns NoPlayersInTeams when one of the teams doesn't have players`() = runTest {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = tourOf(league = league, season = season)
        val homeTeamId = teamIdOf(1)
        val awayTeamId = teamIdOf(2)
        val matchStatistics = matchStatisticsOf(
            home = matchTeamOf(teamId = homeTeamId),
            away = matchTeamOf(teamId = awayTeamId)
        )
        insert(league)
        insert(tour)
        insert(
            InsertTeam(
                team = teamOf(id = homeTeamId),
                tour = tour,
            ),
            InsertTeam(
                team = teamOf(id = awayTeamId),
                tour = tour,
            )
        )

        // WHEN
        val result = storage.insert(matchStatistics, tour.id)

        // THEN
        result.assertFailure {
            assertTrue(this is InsertMatchStatisticsError.NoPlayersInTeams)
        }
    }

    @Test
    fun `insert returns PlayerNotFound when some player is not in the database`() = runTest {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = tourOf(league = league, season = season)
        val homeTeamId = teamIdOf(1)
        val awayTeamId = teamIdOf(2)
        val playerId = playerIdOf(1)
        val matchStatistics = matchStatisticsOf(
            home = matchTeamOf(
                teamId = homeTeamId,
                players = listOf(playerId),
            ),
            away = matchTeamOf(
                teamId = awayTeamId,
                players = listOf(playerIdOf())
            ),
        )
        insert(league)
        insert(tour)
        insert(
            InsertTeam(
                team = teamOf(id = homeTeamId),
                tour = tour,
            ),
            InsertTeam(
                team = teamOf(id = awayTeamId),
                tour = tour,
            )
        )

        // WHEN
        val result = storage.insert(matchStatistics, tour.id)

        // THEN
        result.assertFailure {
            require(this is InsertMatchStatisticsError.PlayerNotFound)
            assertTrue(this.playerIds.contains(playerId to homeTeamId))
        }
    }

    @Test
    fun `insert returns Success when there is at least one player in each team`() = runTest {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = tourOf(league = league, season = season)
        val homeTeamId = teamIdOf(1)
        val awayTeamId = teamIdOf(2)
        val playerId = playerIdOf(1)
        val awayPlayerId = playerIdOf(2)
        val matchStatistics = matchStatisticsOf(
            home = matchTeamOf(
                teamId = homeTeamId,
                players = listOf(playerId),
            ),
            away = matchTeamOf(
                teamId = awayTeamId,
                players = listOf(awayPlayerId)
            ),
            mvp = playerId,
        )
        insert(league)
        insert(tour)
        insert(
            InsertTeam(
                team = teamOf(id = homeTeamId),
                tour = tour,
            ),
            InsertTeam(
                team = teamOf(id = awayTeamId),
                tour = tour,
            )
        )
        insert(
            InsertPlayer(
                player = playerWithDetailsOf(
                    teamPlayer = teamPlayerOf(
                        id = playerId,
                        team = homeTeamId,
                    ),
                ),
                tour = tour,
            ),
            InsertPlayer(
                player = playerWithDetailsOf(
                    teamPlayer = teamPlayerOf(
                        id = awayPlayerId,
                        team = awayTeamId,
                    ),
                ),
                tour = tour,
            )
        )

        // WHEN
        val result = storage.insert(matchStatistics, tour.id)

        // THEN
        result.assertSuccess()
    }
}