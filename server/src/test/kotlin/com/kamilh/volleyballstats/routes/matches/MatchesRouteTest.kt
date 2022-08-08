package com.kamilh.volleyballstats.routes.matches

import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.api.match.MatchResponse
import com.kamilh.volleyballstats.routes.testServerApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonArray
import org.junit.Test
import kotlin.test.assertEquals

class MatchesRouteTest {

    @Test
    fun `endpoint returns Forbidden when unauthorized`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val request = statsApi.getMatches(tourId).toHttpRequest()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `endpoint returns BadRequest when tourId is missing`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val request = statsApi.getMatches(tourId).copy(queryParams = emptyMap()).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `endpoint returns BadRequest when tourId is of different type than Integer`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val request = statsApi.getMatches(tourId).copy(queryParams = mapOf("tourId" to "string")).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `endpoint returns NotFound when no tours in database`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val request = statsApi.getMatches(tourId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `endpoint returns empty list when tour is in database, but with no matches`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val league = leagueOf()
        val tour = tourOf(id = tourId, league = league)
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
        }
        val request = statsApi.getMatches(tourId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.OK, response.status)
        assert(response.body<JsonArray>().isEmpty())
    }

    @Test
    fun `endpoint returns correct matches when tour and matches are in the database`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val league = leagueOf()
        val tour = tourOf(id = tourId, league = league)
        val teamId = teamIdOf(value = 1)
        val teams = listOf(teamOf(id = teamId))
        val matches = listOf(matchOf(home = teamId, away = teamId))
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
            teamStorage.insert(teams, tourId)
            matchStorage.insertOrUpdate(matches, tourId)
        }
        val request = statsApi.getMatches(tourId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        val mapped = response.body<List<MatchResponse>>().map(mappers.matchMapper::from)
        assertEquals(matches, mapped)
    }

    @Test
    fun `saving more players in the database results in more players returned from the endpoint`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val league = leagueOf()
        val tour = tourOf(id = tourId, league = league)
        val teamId = teamIdOf(value = 1)
        val teams = listOf(teamOf(id = teamId))
        val matches = (0..2).map {
            matchOf(id = matchIdOf(value = it.toLong()), home = teamId, away = teamId)
        }
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
            teamStorage.insert(teams, tourId)
            matchStorage.insertOrUpdate(matches.take(1), tourId)
        }
        val request = statsApi.getMatches(tourId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        val mapped = response.body<List<MatchResponse>>().map(mappers.matchMapper::from)
        assertEquals(matches.take(1), mapped)

        withStorages {
            matchStorage.insertOrUpdate(matches.takeLast(2), tourId)
        }

        // WHEN
        val newResponse = client.request(request)

        // THEN
        val newMapped = newResponse.body<List<MatchResponse>>().map(mappers.matchMapper::from)
        assertEquals(matches, newMapped)
    }
}