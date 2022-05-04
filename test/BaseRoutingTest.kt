package com.kamilh

import com.kamilh.authorization.credentialsValidatorOf
import com.kamilh.models.TestAppConfig
import com.kamilh.routes.RoutesModule
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

open class BaseRoutingTest {

    protected val json: Json = Json

    protected fun testRoute(
        request: Request,
        routesModule: RoutesModule = routesModuleOf(),
    ) : TestApplicationCall =
        withTestApplication({
            module(
                appConfig = TestAppConfig(),
                routesModule = routesModule,
            )
        }) {
            handleRequest {
                uri = request.uri
                method = request.method
                request.headers.forEach { (value, key) ->
                    addHeader(
                        name = value,
                        value = key
                    )
                }
                request.body?.let {
                    setBody(it.toJsonBody())
                }
            }
        }

    fun testThatEndpointNeedsSubscriptionKey(uri: String, method: HttpMethod) {
        // GIVEN
        val credentialsValidator = credentialsValidatorOf(
            accessTokenIsValid = false,
        )

        // WHEN
        val response = testRoute(
            request = Request(
                uri = uri,
                method = method,
                headers = mapOf(
                    "StatsApi-Access-Token" to "Access",
                )
            ),
            routesModule = routesModuleOf(
                credentialsValidator = credentialsValidator,
            )
        )

        // THEN
        assert(response.response.status() == HttpStatusCode.Forbidden)
    }

    fun testThatEndpointNeedsAccessToken(uri: String, method: HttpMethod) {
        // GIVEN
        val credentialsValidator = credentialsValidatorOf(
            accessTokenIsValid = false,
        )

        // WHEN
        val response = testRoute(
            request = Request(
                uri = uri,
                method = method,
                headers = mapOf(
                    "StatsApi-Access-Token" to "Access",
                )
            ),
            routesModule = routesModuleOf(
                credentialsValidator = credentialsValidator,
            )
        )

        // THEN
        assert(response.response.status() == HttpStatusCode.Forbidden)
    }

    private fun Any.toJsonBody(): String = json.encodeToString(this)

    protected inline fun <reified T> TestApplicationCall.body(): T? = this.response.content?.let(json::decodeFromString)

    protected data class Request(
        val uri: String,
        val method: HttpMethod,
        val headers: Map<String, String> = emptyMap(),
        val body: Any? = null,
    )
}