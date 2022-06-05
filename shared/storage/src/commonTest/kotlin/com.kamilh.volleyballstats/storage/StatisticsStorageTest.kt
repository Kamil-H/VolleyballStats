package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.MatchStatistics
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.assertSuccess
import kotlinx.coroutines.flow.first
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class StatisticsStorageTest : DatabaseTest() {

    protected val storage: SqlMatchStatisticsStorage by lazy {
        SqlMatchStatisticsStorage(
            queryRunner = testQueryRunner,
            teamQueries = teamQueries,
            teamPlayerQueries = teamPlayerQueries,
            tourTeamQueries = tourTeamQueries,
            matchStatisticsQueries = matchStatisticsQueries,
            playQueries = playQueries,
            playAttackQueries = playAttackQueries,
            playBlockQueries = playBlockQueries,
            playDigQueries = playDigQueries,
            playFreeballQueries = playFreeballQueries,
            playReceiveQueries = playReceiveQueries,
            playServeQueries = playServeQueries,
            playSetQueries = playSetQueries,
            pointQueries = pointQueries,
            pointLineupQueries = pointLineupQueries,
            setQueries = setQueries,
            matchAppearanceQueries = matchAppearanceQueries,
            tourQueries = tourQueries,
        )
    }

    protected suspend fun load(
        league: League = leagueOf(),
        season: Season = seasonOf(2020),
        matchId: MatchId = matchIdOf(),
    ): MatchStatistics {
        // GIVEN
        val matchStatistics = getMatchStatistics(matchId)
        val tour = tourOf(league = league, season = season)

        val homeId = matchStatistics.home.teamId
        val awayId = matchStatistics.away.teamId
        teamQueries.insert(homeId)
        teamQueries.insert(awayId)

        insert(league)
        insert(tour)
        insert(
            InsertTeam(
                team = teamOf(id = homeId, name = matchStatistics.home.code),
                tour = tour,
            )
        )
        insert(
            InsertTeam(
                team = teamOf(id = awayId, name = matchStatistics.away.code),
                tour = tour,
            )
        )
        matchStatistics.home.players.forEach { teamPlayer ->
            insert(
                InsertPlayer(
                    tour = tour,
                    player = playerWithDetailsOf(
                        teamPlayer = teamPlayerOf(id = teamPlayer, team = homeId),
                    )
                )
            )
        }
        matchStatistics.away.players.forEach { teamPlayer ->
            insert(
                InsertPlayer(
                    tour = tour,
                    player = playerWithDetailsOf(
                        teamPlayer = teamPlayerOf(id = teamPlayer, team = awayId),
                    )
                )
            )
        }

        // WHEN
        val insertResult = storage.insert(matchStatistics, tour.id)

        // THEN
        val result = matchStatisticsQueries.selectAll().executeAsList()
        assertEquals(expected = matchStatistics, storage.getAllMatchStatistics().first().first())
        assertTrue(result.isNotEmpty())
        insertResult.assertSuccess()
        return matchStatistics
    }
}