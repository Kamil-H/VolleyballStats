package com.kamilh.volleyballstats.network

import com.kamilh.volleyballstats.domain.models.Result
import io.ktor.client.engine.mock.*
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertTrue
import io.ktor.client.HttpClient as Ktor

class KtorHttpClientTest {

    private val loggedExceptions = mutableListOf<Exception>()
    private fun httpClientThatRespondsWith(response: MockRequestHandler): HttpClient =
        KtorHttpClient(
            ktor = Ktor(MockEngine) {
                engine {
                    addHandler(response)
                }
            }
        ) { exception ->
            loggedExceptions.add(exception)
        }

    @AfterTest
    fun clear() {
        loggedExceptions.clear()
    }

    @Test
    fun `test if execute Endpoint returns Success when Ktor responds with OK`() = runTest {
        // GIVEN
        val endpoint = endpointOf<Unit>()

        // WHEN
        val resource = httpClientThatRespondsWith { respondOk() }.execute(endpoint)

        // THEN
        assertTrue(resource is Result.Success)
    }

    @Test
    fun `test if execute UrlRequest returns Success when Ktor responds with OK`() = runTest {
        // GIVEN
        val endpoint = urlRequestOf<Unit>()

        // WHEN
        val resource = httpClientThatRespondsWith { respondOk() }.execute(endpoint)

        // THEN
        assertTrue(resource is Result.Success)
    }

    @Test
    fun `test if execute Endpoint returns Failure when Ktor responds with BadRequest`() = runTest {
        // GIVEN
        val endpoint = endpointOf<Unit>()

        // WHEN
        val resource = httpClientThatRespondsWith { respondBadRequest() }.execute(endpoint)

        // THEN
        assertTrue(resource is Result.Failure)
        assertTrue(loggedExceptions.isNotEmpty())
    }

    @Test
    fun `test if execute UrlRequest returns Failure when Ktor responds with BadRequest`() = runTest {
        // GIVEN
        val endpoint = urlRequestOf<Unit>()

        // WHEN
        val resource = httpClientThatRespondsWith { respondBadRequest() }.execute(endpoint)

        // THEN
        assertTrue(resource is Result.Failure)
        assertTrue(loggedExceptions.isNotEmpty())
    }

    @Test
    fun `test if execute Endpoint returns Failure when Ktor throws Exception`() = runTest {
        // GIVEN
        val endpoint = endpointOf<Unit>()

        // WHEN
        val resource = httpClientThatRespondsWith { throw Exception() }.execute(endpoint)

        // THEN
        assertTrue(resource is Result.Failure)
        assertTrue(loggedExceptions.isNotEmpty())
    }

    @Test
    fun `test if execute UrlRequest returns Failure when Ktor throws Exception`() = runTest {
        // GIVEN
        val endpoint = urlRequestOf<Unit>()

        // WHEN
        val resource = httpClientThatRespondsWith { throw Exception() }.execute(endpoint)

        // THEN
        assertTrue(resource is Result.Failure)
        assertTrue(loggedExceptions.isNotEmpty())
    }
}