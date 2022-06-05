package com.kamilh.volleyballstats.routes.matches

import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.routes.testServerApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.junit.Test
import kotlin.test.assertEquals

class MatchReportRouteTest {

    @Test
    fun `endpoint returns Forbidden when unauthorized`() = testServerApplication {
        // GIVEN
        val matchId = matchIdOf()
        val request = api.getMatchReport(matchId).toHttpRequest()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `endpoint returns NotFound when matchId is missing`() = testServerApplication {
        // GIVEN
        val matchId = matchIdOf()
        val originalRequest = api.getMatchReport(matchId)
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
        val originalRequest = api.getMatchReport(matchId)
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
        val request = api.getMatchReport(matchId).toHttpRequest().authorize()

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
        val request = api.getMatchReport(matchId).toHttpRequest().authorize()

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
        val playerWithDetails = listOf(
            playerWithDetailsOf(teamPlayer = teamPlayerOf(id = playerId, team = teamIdOne)),
            playerWithDetailsOf(teamPlayer = teamPlayerOf(id = playerId, team = teamIdTwo))
        )
        val matchStatistics = matchStatisticsOf(
            matchId = matchId,
            home = matchTeamOf(teamId = teamIdOne, players = listOf(playerId)),
            away = matchTeamOf(teamId = teamIdTwo, players = listOf(playerId)),
            mvp = playerId
        )
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
            teamStorage.insert(teams, tourId)
            playerStorage.insert(playerWithDetails, tourId)
            matchStatisticsStorage.insert(matchStatistics, tourId)
        }
        val request = api.getMatchReport(matchId).toHttpRequest().authorize()

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
        val playerWithDetails = listOf(
            playerWithDetailsOf(teamPlayer = teamPlayerOf(id = playerId, team = teamIdOne)),
            playerWithDetailsOf(teamPlayer = teamPlayerOf(id = playerId, team = teamIdTwo))
        )
        val matchStatistics = matchStatisticsOf(
            matchId = matchId,
            home = matchTeamOf(teamId = teamIdOne, players = listOf(playerId)),
            away = matchTeamOf(teamId = teamIdTwo, players = listOf(playerId)),
            mvp = playerId
        )
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
            teamStorage.insert(teams, tourId)
            playerStorage.insert(playerWithDetails, tourId)
            matchStatisticsStorage.insert(matchStatistics, tourId)
        }
        val request = api.getMatchReport(matchId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        val mapped = mappers.matchReportMapper.from(response.body())
        assertEquals(matchStatistics, mapped)

        // GIVEN
        val newMatchId = matchIdOf(value = 2)
        val newMatchStatistics = matchStatistics.copy(matchId = newMatchId)
        withStorages {
            matchStatisticsStorage.insert(newMatchStatistics, tourId)
        }

        // WHEN
        val newRequest = api.getMatchReport(newMatchId).toHttpRequest().authorize()
        val newResponse = client.request(newRequest)

        // THEN
        val newMapped = mappers.matchReportMapper.from(newResponse.body())
        assertEquals(newMatchStatistics, newMapped)
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
        val playerWithDetails = listOf(
            playerWithDetailsOf(teamPlayer = teamPlayerOf(id = playerId, team = teamIdOne)),
            playerWithDetailsOf(teamPlayer = teamPlayerOf(id = playerId, team = teamIdTwo))
        )
        val matchStatistics = matchStatisticsOf(
            matchId = matchId,
            home = matchTeamOf(teamId = teamIdOne, players = listOf(playerId)),
            away = matchTeamOf(teamId = teamIdTwo, players = listOf(playerId)),
            mvp = playerId
        )
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
            teamStorage.insert(teams, tourId)
            playerStorage.insert(playerWithDetails, tourId)
            matchStatisticsStorage.insert(matchStatistics, tourId)
        }
        val request = api.getMatchReport(matchId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        val mapped = mappers.matchReportMapper.from(response.body())
        assertEquals(matchStatistics, mapped)

        // GIVEN
        val newTourId = tourIdOf(1)
        val newTour = tour.copy(id = newTourId, season = tour.season.plus(1))
        val newMatchId = matchIdOf(value = 2)
        val newMatchStatistics = matchStatistics.copy(matchId = newMatchId)
        withStorages {
            tourStorage.insert(newTour)
            teamStorage.insert(teams, newTourId)
            playerStorage.insert(playerWithDetails, newTourId)
            matchStatisticsStorage.insert(newMatchStatistics, newTourId)
        }

        // WHEN
        val newRequest = api.getMatchReport(newMatchId).toHttpRequest().authorize()
        val newResponse = client.request(newRequest)

        // THEN
        val newMapped = mappers.matchReportMapper.from(newResponse.body())
        assertEquals(newMatchStatistics, newMapped)
    }
}