package com.kamilh

import com.kamilh.models.Country
import io.ktor.client.request.*
import org.junit.Test

class TourRoutesTest {

    @Test
    fun test() = applicationTest {
        // GIVEN
        withDatabase {
            leagueQueries.insert(Country.POLAND, 1)
        }

        // WHEN
        val response = client.request(testComponent.api.getTours().toHttpRequest())

        // THEN
        println(response)
    }
}