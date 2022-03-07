package com.kamilh.storage

import com.kamilh.models.*
import com.kamilh.repository.polishleague.seasonOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SqlMatchStatisticsStorageTest : StatisticsStorageTest() {

    @Test
    fun `insert and select works properly`() = runBlocking {
        load(matchReportId = matchReportIdOf(2101911L))
        Unit
    }

    @Test
    fun `insert returns TourNotFound when there is no such tour`() = runBlocking {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val matchStatistics = matchStatisticsOf()

        // WHEN
        val result = storage.insert(matchStatistics, league, season, matchIdOf())

        // THEN
        result.assertFailure {
            assert(this == InsertMatchStatisticsError.TourNotFound)
        }
    }

    @Test
    fun `insert returns TeamNotFound when there is no such team`() = runBlocking {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = tourOf(league = league, season = season)
        val teamId = teamIdOf(1)
        val matchStatistics = matchStatisticsOf(home = matchTeamOf(teamId = teamId))
        insert(league)
        insert(tour)

        // WHEN
        val result = storage.insert(matchStatistics, league, season, matchIdOf())

        // THEN
        result.assertFailure {
            require(this is InsertMatchStatisticsError.TeamNotFound)
            assert(this.teamId == teamId)
        }
    }

    @Test
    fun `insert updates Home's team code`() = runBlocking {
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
                league = league,
                season = season,
            )
        )

        // WHEN
        val result = storage.insert(matchStatistics, league, season, matchIdOf())

        // THEN
        assert(teamQueries.selectAll().executeAsList().first { it.id == homeTeamId }.code == code)
        result.assertFailure {
            require(this is InsertMatchStatisticsError.TeamNotFound)
            assert(this.teamId == awayTeamId)
        }
    }

    @Test
    fun `insert updates Away's team code`() = runBlocking {
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
                league = league,
                season = season,
            ),
            InsertTeam(
                team = teamOf(id = awayTeamId),
                league = league,
                season = season,
            )
        )

        // WHEN
        storage.insert(matchStatistics, league, season, matchIdOf())

        // THEN
        assert(teamQueries.selectAll().executeAsList().first { it.id == awayTeamId }.code == code)
    }

    @Test
    fun `insert returns NoPlayersInTeams when one of the teams doesn't have players`() = runBlocking {
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
                league = league,
                season = season,
            ),
            InsertTeam(
                team = teamOf(id = awayTeamId),
                league = league,
                season = season,
            )
        )

        // WHEN
        val result = storage.insert(matchStatistics, league, season, matchIdOf())

        // THEN
        result.assertFailure {
            assert(this is InsertMatchStatisticsError.NoPlayersInTeams)
        }
    }

    @Test
    fun `insert returns PlayerNotFound when some player is not in the database`() = runBlocking {
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
                players = listOf(matchPlayerOf(id = playerId)),
            ),
            away = matchTeamOf(
                teamId = awayTeamId,
                players = listOf(matchPlayerOf())
            ),
        )
        insert(league)
        insert(tour)
        insert(
            InsertTeam(
                team = teamOf(id = homeTeamId),
                league = league,
                season = season,
            ),
            InsertTeam(
                team = teamOf(id = awayTeamId),
                league = league,
                season = season,
            )
        )

        // WHEN
        val result = storage.insert(matchStatistics, league, season, matchIdOf())

        // THEN
        result.assertFailure {
            require(this is InsertMatchStatisticsError.PlayerNotFound)
            assert(this.playerIds.contains(playerId to homeTeamId))
        }
    }

    @Test
    fun `insert returns updates Player's information`() = runBlocking {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = tourOf(league = league, season = season)
        val homeTeamId = teamIdOf(1)
        val awayTeamId = teamIdOf(2)
        val playerId = playerIdOf(1)
        val firstName = "firstName"
        val lastName = "lastName"
        val isForeign = false
        val matchStatistics = matchStatisticsOf(
            home = matchTeamOf(
                teamId = homeTeamId,
                players = listOf(
                    matchPlayerOf(
                        id = playerId,
                        firstName = firstName,
                        lastName = lastName,
                        isForeign = isForeign,
                    ),
                ),
            ),
            away = matchTeamOf(
                teamId = awayTeamId,
                players = listOf(matchPlayerOf())
            ),
        )
        insert(league)
        insert(tour)
        insert(
            InsertTeam(
                team = teamOf(id = homeTeamId),
                league = league,
                season = season,
            ),
            InsertTeam(
                team = teamOf(id = awayTeamId),
                league = league,
                season = season,
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
                league = league,
                season = season,
            )
        )

        // WHEN
        storage.insert(matchStatistics, league, season, matchIdOf())

        // THEN
        val player = playerQueries.selectPlayerById(playerId).executeAsOne()
        assert(player.first_name == firstName)
        assert(player.last_name == lastName)
        assert(player.is_foreign == isForeign)
    }

    @Test
    fun `insert returns Success when there is at least one player in each team`() = runBlocking {
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
                players = listOf(matchPlayerOf(id = playerId)),
            ),
            away = matchTeamOf(
                teamId = awayTeamId,
                players = listOf(matchPlayerOf(id = awayPlayerId))
            ),
            mvp = playerId,
        )
        insert(league)
        insert(tour)
        insert(
            InsertTeam(
                team = teamOf(id = homeTeamId),
                league = league,
                season = season,
            ),
            InsertTeam(
                team = teamOf(id = awayTeamId),
                league = league,
                season = season,
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
                league = league,
                season = season,
            ),
            InsertPlayer(
                player = playerWithDetailsOf(
                    teamPlayer = teamPlayerOf(
                        id = awayPlayerId,
                        team = awayTeamId,
                    ),
                ),
                league = league,
                season = season,
            )
        )

        // WHEN
        val result = storage.insert(matchStatistics, league, season, matchIdOf())

        // THEN
        result.assertSuccess()
    }
}

fun matchStatisticsStorageOf(
    insert: (matchStatistics: MatchStatistics, league: League, season: Season, matchId: MatchId) -> InsertMatchStatisticsResult = { _, _, _, _ ->
        InsertMatchStatisticsResult.success(Unit)
    },
    getAllMatchStatistics: Flow<List<MatchStatistics>> = flowOf(emptyList()),
    getMatchStatistics: MatchStatistics? = null,
): MatchStatisticsStorage = object : MatchStatisticsStorage {
    override suspend fun insert(matchStatistics: MatchStatistics, league: League, season: Season, matchId: MatchId): InsertMatchStatisticsResult =
        insert(matchStatistics, league, season, matchId)

    override suspend fun getAllMatchStatistics(league: League, season: Season): Flow<List<MatchStatistics>> = getAllMatchStatistics

    override suspend fun getMatchStatistics(matchReportId: MatchReportId): MatchStatistics? = getMatchStatistics
}