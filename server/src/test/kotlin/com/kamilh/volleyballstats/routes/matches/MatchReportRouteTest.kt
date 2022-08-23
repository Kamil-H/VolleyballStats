package com.kamilh.volleyballstats.routes.matches

import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.domain.player.playerOf
import com.kamilh.volleyballstats.routes.testServerApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import org.junit.Test
import kotlin.test.assertEquals

class MatchReportRouteTest {

    @Test
    fun `endpoint returns Forbidden when unauthorized`() = testServerApplication {
        // GIVEN
        val matchId = matchIdOf()
        val request = statsApi.getMatchReport(matchId).toHttpRequest()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `endpoint returns NotFound when matchId is missing`() = testServerApplication {
        // GIVEN
        val matchId = matchIdOf()
        val originalRequest = statsApi.getMatchReport(matchId)
        val request = originalRequest.copy(path = originalRequest.path.replace("/${matchId.value}", ""))
            .toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `endpoint returns BadRequest when matchId is of different type than Integer`() = testServerApplication {
        // GIVEN
        val matchId = matchIdOf()
        val originalRequest = statsApi.getMatchReport(matchId)
        val request = originalRequest.copy(path = originalRequest.path.replace("/${matchId.value}", "/id"))
            .toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `endpoint returns NotFound when no tours in database`() = testServerApplication {
        // GIVEN
        val matchId = matchIdOf()
        val request = statsApi.getMatchReport(matchId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `endpoint returns NotFound when tour is in database, but with no matches`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val league = leagueOf()
        val tour = tourOf(id = tourId, league = league)
        val matchId = matchIdOf()
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
        }
        val request = statsApi.getMatchReport(matchId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `endpoint returns correct matches when tour and matches are in the database`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val league = leagueOf()
        val tour = tourOf(id = tourId, league = league)
        val teamIdOne = teamIdOf(value = 1)
        val teamIdTwo = teamIdOf(value = 2)
        val matchId = matchIdOf()
        val teams = listOf(teamOf(id = teamIdOne), teamOf(id = teamIdTwo))
        val playerId = playerIdOf(value = 1)
        val players = listOf(
            playerOf(id = playerId, team = teamIdOne),
            playerOf(id = playerId, team = teamIdTwo),
        )
        val matchStatistics = matchReportOf(
            matchId = matchId,
            home = matchTeamOf(teamId = teamIdOne, players = listOf(playerId)),
            away = matchTeamOf(teamId = teamIdTwo, players = listOf(playerId)),
            mvp = playerId
        )
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
            teamStorage.insert(teams, tourId)
            playerStorage.insert(players, tourId)
            matchReportStorage.insert(matchStatistics, tourId)
        }
        val request = statsApi.getMatchReport(matchId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        val mapped = mappers.matchReportMapper.from(response.body())
        assertEquals(matchStatistics, mapped)
    }

    @Test
    fun `saving more matches in the database results in more matches returned from the endpoint`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val league = leagueOf()
        val tour = tourOf(id = tourId, league = league)
        val teamIdOne = teamIdOf(value = 1)
        val teamIdTwo = teamIdOf(value = 2)
        val matchId = matchIdOf(value = 1)
        val teams = listOf(teamOf(id = teamIdOne), teamOf(id = teamIdTwo))
        val playerId = playerIdOf(value = 1)
        val players = listOf(
            playerOf(id = playerId, team = teamIdOne),
            playerOf(id = playerId, team = teamIdTwo),
        )
        val matchStatistics = matchReportOf(
            matchId = matchId,
            home = matchTeamOf(teamId = teamIdOne, players = listOf(playerId)),
            away = matchTeamOf(teamId = teamIdTwo, players = listOf(playerId)),
            mvp = playerId
        )
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
            teamStorage.insert(teams, tourId)
            playerStorage.insert(players, tourId)
            matchReportStorage.insert(matchStatistics, tourId)
        }
        val request = statsApi.getMatchReport(matchId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        val mapped = mappers.matchReportMapper.from(response.body())
        assertEquals(matchStatistics, mapped)

        // GIVEN
        val newMatchId = matchIdOf(value = 2)
        val newMatchReport = matchStatistics.copy(matchId = newMatchId)
        withStorages {
            matchReportStorage.insert(newMatchReport, tourId)
        }

        // WHEN
        val newRequest = statsApi.getMatchReport(newMatchId).toHttpRequest().authorize()
        delay(100)
        val newResponse = client.request(newRequest)

        // THEN
        val newMapped = mappers.matchReportMapper.from(newResponse.body())
        assertEquals(newMatchReport, newMapped)
    }

    @Test
    fun `saving more matches from different tour in the database results in more matches returned from the endpoint`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val league = leagueOf()
        val tour = tourOf(id = tourId, league = league)
        val teamIdOne = teamIdOf(value = 1)
        val teamIdTwo = teamIdOf(value = 2)
        val matchId = matchIdOf(value = 1)
        val teams = listOf(teamOf(id = teamIdOne), teamOf(id = teamIdTwo))
        val playerId = playerIdOf(value = 1)
        val players = listOf(
            playerOf(id = playerId, team = teamIdOne),
            playerOf(id = playerId, team = teamIdTwo),
        )
        val matchReport = matchReportOf(
            matchId = matchId,
            home = matchTeamOf(teamId = teamIdOne, players = listOf(playerId)),
            away = matchTeamOf(teamId = teamIdTwo, players = listOf(playerId)),
            mvp = playerId
        )
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
            teamStorage.insert(teams, tourId)
            playerStorage.insert(players, tourId)
            matchReportStorage.insert(matchReport, tourId)
        }
        val request = statsApi.getMatchReport(matchId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        val mapped = mappers.matchReportMapper.from(response.body())
        assertEquals(matchReport, mapped)

        // GIVEN
        val newTourId = tourIdOf(1)
        val newTour = tour.copy(id = newTourId, season = tour.season.plus(1))
        val newMatchId = matchIdOf(value = 2)
        val newMatchReport = matchReport.copy(matchId = newMatchId)
        withStorages {
            tourStorage.insert(newTour)
            teamStorage.insert(teams, newTourId)
            playerStorage.insert(players, newTourId)
            matchReportStorage.insert(newMatchReport, newTourId)
        }

        // WHEN
        val newRequest = statsApi.getMatchReport(newMatchId).toHttpRequest().authorize()
        delay(100)
        val newResponse = client.request(newRequest)

        // THEN
        val newMapped = mappers.matchReportMapper.from(newResponse.body())
        assertEquals(newMatchReport, newMapped)
    }
}