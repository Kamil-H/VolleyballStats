package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.assertSuccess
import com.kamilh.volleyballstats.domain.leagueOf
import com.kamilh.volleyballstats.domain.matchIdOf
import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.MatchReport
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.player.playerOf
import com.kamilh.volleyballstats.domain.seasonOf
import com.kamilh.volleyballstats.domain.teamOf
import com.kamilh.volleyballstats.domain.tourOf
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class ReportStorageTest : DatabaseTest() {

    protected val storage: SqlMatchReportStorage by lazy {
        SqlMatchReportStorage(
            queryRunner = testQueryRunner,
            teamQueries = teamQueries,
            teamPlayerQueries = teamPlayerQueries,
            tourTeamQueries = tourTeamQueries,
            matchReportQueries = matchReportQueries,
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
    ): MatchReport {
        // GIVEN
        val matchStatistics = getMatchReport(matchId)
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
                    player = playerOf(id = teamPlayer, team = homeId)
                )
            )
        }
        matchStatistics.away.players.forEach { teamPlayer ->
            insert(
                InsertPlayer(
                    tour = tour,
                    player = playerOf(id = teamPlayer, team = awayId)
                )
            )
        }

        // WHEN
        val insertResult = storage.insert(matchStatistics, tour.id)

        // THEN
        val result = matchReportQueries.selectAll().executeAsList()
        val saved = storage.getMatchReport(matchId)!!
        assertEquals(expected = matchStatistics.matchId, saved.matchId)
        assertEquals(expected = matchStatistics.home, saved.home)
        assertEquals(expected = matchStatistics.away, saved.away)
        assertEquals(expected = matchStatistics.mvp, saved.mvp)
        assertEquals(expected = matchStatistics.bestPlayer, saved.bestPlayer)
        assertEquals(expected = matchStatistics.updatedAt, saved.updatedAt)
        assertEquals(expected = matchStatistics.phase, saved.phase)
        assertEquals(expected = matchStatistics.sets.size, saved.sets.size)
        matchStatistics.sets.forEachIndexed { index, matchSet ->
            val savedSet = saved.sets[index]
            assertEquals(expected = matchSet.number, savedSet.number)
            assertEquals(expected = matchSet.score, savedSet.score)
            assertEquals(expected = matchSet.startTime, savedSet.startTime)
            assertEquals(expected = matchSet.endTime, savedSet.endTime)
            assertEquals(expected = matchSet.duration, savedSet.duration)
            assertEquals(expected = matchSet.points.size, savedSet.points.size)
            matchSet.points.forEachIndexed { pointIndex, matchPoint ->
                val savedPoint = savedSet.points[pointIndex]
                assertEquals(expected = matchPoint.score, savedPoint.score)
                assertEquals(expected = matchPoint.startTime, savedPoint.startTime)
                assertEquals(expected = matchPoint.endTime, savedPoint.endTime)
                assertEquals(expected = matchPoint.point, savedPoint.point)
                assertEquals(expected = matchPoint.homeLineup, savedPoint.homeLineup)
                assertEquals(expected = matchPoint.awayLineup, savedPoint.awayLineup)
                assertEquals(expected = matchPoint.playActions.size, savedPoint.playActions.size)
                assertEquals(expected = matchPoint.playActions, savedPoint.playActions)
            }
        }
        assertTrue(result.isNotEmpty())
        insertResult.assertSuccess()
        return matchStatistics
    }
}