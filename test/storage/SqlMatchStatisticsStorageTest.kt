package com.kamilh.storage

import com.kamilh.models.*
import com.kamilh.repository.polishleague.seasonOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.Test

class SqlMatchStatisticsStorageTest : StatisticsStorageTest() {

    @Test
    fun `insert and select works properly`() = runTest {
        load(matchReportId = matchReportIdOf(2101911L))
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
            assert(this == InsertMatchStatisticsError.TourNotFound)
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
            assert(this.teamId == teamId)
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
        assert(teamQueries.selectAll().executeAsList().first { it.id == homeTeamId }.code == code)
        result.assertFailure {
            require(this is InsertMatchStatisticsError.TeamNotFound)
            assert(this.teamId == awayTeamId)
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
        assert(teamQueries.selectAll().executeAsList().first { it.id == awayTeamId }.code == code)
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
            assert(this is InsertMatchStatisticsError.NoPlayersInTeams)
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
            assert(this.playerIds.contains(playerId to homeTeamId))
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

fun matchStatisticsStorageOf(
    insert: (matchStatistics: MatchStatistics, tourId: TourId) -> InsertMatchStatisticsResult = { _, _ ->
        InsertMatchStatisticsResult.success(Unit)
    },
    getAllMatchStatistics: Flow<List<MatchStatistics>> = flowOf(emptyList()),
): MatchStatisticsStorage = object : MatchStatisticsStorage {
    override suspend fun insert(matchStatistics: MatchStatistics, tourId: TourId): InsertMatchStatisticsResult =
        insert(matchStatistics, tourId)

    override fun getAllMatchStatistics(): Flow<List<MatchStatistics>> = getAllMatchStatistics
}