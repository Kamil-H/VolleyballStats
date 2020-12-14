package com.kamilh

import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RootEndpointTest {

    private fun testRootCallWithHeaders(headers: Map<String, String> = emptyMap(), expectedStatusCode: HttpStatusCode) {
        withTestApplication({ module() }) {
            handleRequest {
                uri = "/"
                method = HttpMethod.Get
                headers.forEach { (value, key) ->
                    addHeader(
                        name = key,
                        value = value
                    )
                }
            }.apply {
                assertEquals(expectedStatusCode, response.status())
            }
        }
    }

    @Test
    fun `test root call without headers`() {
        testRootCallWithHeaders(expectedStatusCode = HttpStatusCode.Forbidden)
    }

    @Test
    fun `test root call with StatsApi-Subscription-Key`() {
        testRootCallWithHeaders(
            headers = mapOf("StatsApi-Subscription-Key" to ""),
            expectedStatusCode = HttpStatusCode.Forbidden
        )
    }

    @Test
    fun `test root call with StatsApi-Access-Token`() {
        testRootCallWithHeaders(
            headers = mapOf("StatsApi-Access-Token" to ""),
            expectedStatusCode = HttpStatusCode.Forbidden
        )
    }
}
