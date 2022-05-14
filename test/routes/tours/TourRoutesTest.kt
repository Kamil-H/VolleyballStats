package com.kamilh.routes.tours

import com.kamilh.models.api.tour.TourResponse
import com.kamilh.models.leagueOf
import com.kamilh.models.tourIdOf
import com.kamilh.models.tourOf
import com.kamilh.routes.testServerApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonArray
import org.junit.Test
import kotlin.test.assertEquals

class TourRoutesTest {

    @Test
    fun `endpoint returns Forbidden when unauthorized`() = testServerApplication {
        // GIVEN
        val request = api.getTours().toHttpRequest()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `endpoint returns Ok when authorized`() = testServerApplication {
        // GIVEN
        val request = api.getTours().toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `endpoint returns empty list when no tours in database`() = testServerApplication {
        // GIVEN
        val request = api.getTours().toHttpRequest().authorize()

        // WHEN
        val response = client.request(request)

        // THEN
        assertEquals(HttpStatusCode.OK, response.status)
        assert(response.body<JsonArray>().isEmpty())
    }

    @Test
    fun `endpoint returns tours from the database`() = testServerApplication {
        // GIVEN
        val request = api.getTours().toHttpRequest().authorize()
        val league = leagueOf()
        val tour = tourOf(league = league)
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
        }

        // WHEN
        val response = client.request(request)

        // THEN
        val mapped = response.body<List<TourResponse>>().map(mappers.tourMapper::from)
        assertEquals(listOf(tour), mapped)
    }

    @Test
    fun `saving more tours in the database results in more tours returned from the endpoint`() = testServerApplication {
        // GIVEN
        val request = api.getTours().toHttpRequest().authorize()
        val league = leagueOf()
        val tour = tourOf(league = league)
        withStorages {
            leagueStorage.insert(league)
            tourStorage.insert(tour)
        }

        // WHEN
        val response = client.request(request)

        // THEN
        val mapped = response.body<List<TourResponse>>().map(mappers.tourMapper::from)
        assertEquals(listOf(tour), mapped)

        // GIVEN
        val newTour = tour.copy(
            id = tourIdOf(value = 1),
            season = tour.season + 1,
            league = league,
        )
        withStorages {
            tourStorage.insert(newTour)
        }

        // WHEN
        val newResponse = client.request(request)

        // THEN
        val newMapped = newResponse.body<List<TourResponse>>().map(mappers.tourMapper::from)
        assertEquals(listOf(tour, newTour), newMapped)
    }
}