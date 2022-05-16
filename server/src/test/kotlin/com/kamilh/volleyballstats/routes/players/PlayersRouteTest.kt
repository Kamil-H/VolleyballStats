package com.kamilh.volleyballstats.routes.players

import com.kamilh.volleyballstats.models.*
import com.kamilh.volleyballstats.models.api.player_with_details.PlayerWithDetailsResponse
import com.kamilh.volleyballstats.routes.testServerApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonArray
import org.junit.Test
import kotlin.test.assertEquals

class PlayersRouteTest {

    @Test
    fun `endpoint returns Forbidden when unauthorized`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val request = api.getPlayers(tourId).toHttpRequest()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `endpoint returns BadRequest when tourId is missing`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val request = api.getPlayers(tourId).copy(queryParams = emptyMap()).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `endpoint returns BadRequest when tourId is of different type than Integer`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val request = api.getPlayers(tourId).copy(queryParams = mapOf("tourId" to "string")).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `endpoint returns NotFound when no tours in database`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val request = api.getPlayers(tourId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `endpoint returns empty list when tour is in database, but with no players`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val league = leagueOf()
        val tour = tourOf(id = tourId, league = league)
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
        }
        val request = api.getPlayers(tourId).toHttpRequest().authorize()

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
        val teamId = teamIdOf(value = 1)
        val teams = listOf(teamOf(id = teamId))
        val players = listOf(playerWithDetailsOf(teamPlayer = teamPlayerOf(team = teamId)))
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
            teamStorage.insert(teams, tourId)
            playerStorage.insert(players, tourId)
        }
        val request = api.getPlayers(tourId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        val mapped = response.body<List<PlayerWithDetailsResponse>>().map(mappers.playerWithDetailsMapper::from)
        assertEquals(players, mapped)
    }

    @Test
    fun `saving more players in the database results in more players returned from the endpoint`() = testServerApplication {
        // GIVEN
        val tourId = tourIdOf()
        val league = leagueOf()
        val tour = tourOf(id = tourId, league = league)
        val teamId = teamIdOf(value = 1)
        val teams = listOf(teamOf(id = teamId))
        val players = listOf(playerWithDetailsOf(teamPlayer = teamPlayerOf(team = teamId)))
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
            teamStorage.insert(teams, tourId)
            playerStorage.insert(players, tourId)
        }
        val request = api.getPlayers(tourId).toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        val mapped = response.body<List<PlayerWithDetailsResponse>>().map(mappers.playerWithDetailsMapper::from)
        assertEquals(players, mapped)

        val newTeamId = teamIdOf(value = 2)
        val newTeams = listOf(teamOf(id = newTeamId))
        val newPlayers = listOf(
            playerWithDetailsOf(teamPlayer = teamPlayerOf(id = playerIdOf(1), team = newTeamId)),
            playerWithDetailsOf(teamPlayer = teamPlayerOf(id = playerIdOf(2), team = newTeamId))
        )
        withStorages {
            teamStorage.insert(newTeams, tourId)
            playerStorage.insert(newPlayers, tourId)
        }

        // WHEN
        val newResponse = client.request(request)

        // THEN
        val newMapped = newResponse.body<List<PlayerWithDetailsResponse>>().map(mappers.playerWithDetailsMapper::from)
        assertEquals(players + newPlayers, newMapped)
    }
}