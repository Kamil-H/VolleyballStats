package com.kamilh.volleyballstats.routes.teams

import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.api.team.TeamResponse
import com.kamilh.volleyballstats.routes.testServerApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonArray
import org.junit.Test
import kotlin.test.assertEquals

class TeamsRouteTest {

    @Test
    fun `endpoint returns Forbidden when unauthorized`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val request = statsApi.getTeams(tourId).toHttpRequest()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `endpoint returns BadRequest when tourId is missing`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val request = statsApi.getTeams(tourId).copy(queryParams = emptyMap()).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `endpoint returns BadRequest when tourId is of different type than Integer`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val request = statsApi.getTeams(tourId).copy(queryParams = mapOf("tourId" to "string")).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `endpoint returns NotFound when no tours in database`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val request = statsApi.getTeams(tourId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `endpoint returns empty list when tour is in database, but with no teams`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val league = leagueOf()
        val tour = tourOf(id = tourId, league = league)
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
        }
        val request = statsApi.getTeams(tourId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.OK, response.status)
        assert(response.body<JsonArray>().isEmpty())
    }

    @Test
    fun `endpoint returns correct teams when tour and teams are in the database`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val league = leagueOf()
        val tour = tourOf(id = tourId, league = league)
        val teams = listOf(
            teamOf(id = teamIdOf(value = 1)),
            teamOf(id = teamIdOf(value = 2))
        )
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
            teamStorage.insert(teams, tourId)
        }
        val request = statsApi.getTeams(tourId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        val mapped = response.body<List<TeamResponse>>().map(mappers.teamMapper::from)
        assertEquals(teams, mapped)
    }

    @Test
    fun `saving more tours in the database results in more teams returned from the endpoint`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val league = leagueOf()
        val tour = tourOf(id = tourId, league = league)
        val teams = listOf(
            teamOf(id = teamIdOf(value = 1)),
            teamOf(id = teamIdOf(value = 2))
        )
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
            teamStorage.insert(teams, tourId)
        }
        val request = statsApi.getTeams(tourId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        val mapped = response.body<List<TeamResponse>>().map(mappers.teamMapper::from)
        assertEquals(teams, mapped)

        val newTeams = listOf(
            teamOf(id = teamIdOf(value = 3)),
            teamOf(id = teamIdOf(value = 4))
        )
        withStorages {
            teamStorage.insert(newTeams, tourId)
        }

        // WHEN
        val newResponse = client.request(request)

        // THEN
        val newMapped = newResponse.body<List<TeamResponse>>().map(mappers.teamMapper::from)
        assertEquals(teams + newTeams, newMapped)
    }

    @Test
    fun `endpoint returns data for correct tourId`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val league = leagueOf()
        val tour = tourOf(id = tourId, league = league)
        val teams = listOf(
            teamOf(id = teamIdOf(value = 1)),
            teamOf(id = teamIdOf(value = 2))
        )
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
            teamStorage.insert(teams, tourId)
        }
        val request = statsApi.getTeams(tourId).toHttpRequest().authorize()

        // WHEN
        client.request(request)

        val newTourId = tourIdOf(value = 1)
        val newTour = tourOf(
            id = tourIdOf(value = 1),
            season = tour.season + 1,
            league = league,
        )
        val newTeams = listOf(
            teamOf(id = teamIdOf(value = 3)),
            teamOf(id = teamIdOf(value = 4))
        )
        withStorages {
            tourStorage.insert(newTour)
            teamStorage.insert(newTeams, newTourId)
        }
        val newRequest = statsApi.getTeams(newTourId).toHttpRequest().authorize()

        // WHEN
        val newResponse = client.request(newRequest)

        // THEN
        val newMapped = newResponse.body<List<TeamResponse>>().map(mappers.teamMapper::from)
        assertEquals(newTeams, newMapped)
    }
}