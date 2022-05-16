package com.kamilh.volleyballstats.repository

import com.kamilh.volleyballstats.models.NetworkResult
import com.kamilh.volleyballstats.models.Result
import com.kamilh.volleyballstats.models.Url
import com.kamilh.volleyballstats.models.httprequest.Endpoint
import com.kamilh.volleyballstats.models.httprequest.HttpRequest
import com.kamilh.volleyballstats.models.httprequest.UrlRequest
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.AfterTest
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

fun <T> httpClientOf(networkResult: NetworkResult<T>): HttpClient =
    object : HttpClient {
        override suspend fun <E> execute(httpRequest: HttpRequest<E>): NetworkResult<E> {
            return when (networkResult) {
                is Result.Failure -> networkResult
                is Result.Success -> (networkResult.value as? E)?.let {
                    NetworkResult.success(it)
                } ?: throw Exception()
            }
        }
    }

inline fun <reified T> endpointOf(
    baseUrl: String = "",
    path: String = "",
    protocol: URLProtocol = URLProtocol.HTTPS,
    queryParams: Map<String, Any?> = emptyMap(),
    method: HttpMethod = HttpMethod.Get,
    body: HttpRequest.Body? = null,
): Endpoint<T> =
    Endpoint.create(
        baseUrl = baseUrl,
        protocol = protocol,
        path = path,
        method = method,
        body = body,
        queryParams = queryParams,
    )

inline fun <reified T> urlRequestOf(
    url: Url = Url.create("google.com"),
    method: HttpMethod = HttpMethod.Get,
    body: HttpRequest.Body? = null,
): UrlRequest<T> =
    UrlRequest.create(
        url = url,
        method = method,
        body = body,
    )