package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.domain.player.playerOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SqlMatchReportStorageTest : ReportStorageTest() {

    @Test
    fun `insert and select works properly`() = runTest {
        load()
        yield()
    }

    @Test
    fun `insert returns TourNotFound when there is no such tour`() = runTest {
        // GIVEN
        val tourId = tourIdOf()
        val matchStatistics = matchReportOf()

        // WHEN
        val result = storage.insert(matchStatistics, tourId)

        // THEN
        result.assertFailure {
            assertEquals(expected = InsertMatchReportError.TourNotFound, this)
        }
    }

    @Test
    fun `insert returns TeamNotFound when there is no such team`() = runTest {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = tourOf(league = league, season = season)
        val teamId = teamIdOf(1)
        val matchStatistics = matchReportOf(home = matchTeamOf(teamId = teamId))
        insert(league)
        insert(tour)

        // WHEN
        val result = storage.insert(matchStatistics, tour.id)

        // THEN
        result.assertFailure {
            require(this is InsertMatchReportError.TeamNotFound)
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
        val matchStatistics = matchReportOf(
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
            require(this is InsertMatchReportError.TeamNotFound)
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
        val matchStatistics = matchReportOf(
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
        val matchStatistics = matchReportOf(
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
            assertTrue(this is InsertMatchReportError.NoPlayersInTeams)
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
        val matchStatistics = matchReportOf(
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
            require(this is InsertMatchReportError.PlayerNotFound)
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
        val matchStatistics = matchReportOf(
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
                player = playerOf(
                    id = playerId,
                    team = homeTeamId,
                ),
                tour = tour,
            ),
            InsertPlayer(
                player = playerOf(
                    id = awayPlayerId,
                    team = awayTeamId,
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