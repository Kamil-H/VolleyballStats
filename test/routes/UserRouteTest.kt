package com.kamilh.routes

import com.kamilh.BaseRoutingTest
import com.kamilh.authorization.credentialsValidatorOf
import com.kamilh.models.Result
import com.kamilh.models.api.UserResponse
import com.kamilh.storage.nullUUID
import com.kamilh.testApplicationModule
import com.kamilh.userControllerOf
import com.kamilh.userResponseOf
import io.ktor.http.*
import org.junit.Test
import routes.CallError
import routes.CallResult

class UserRouteTest : BaseRoutingTest() {

    @Test
    fun `test when calling register endpoint without deviceId in path request is not successful`() {
        // GIVEN
        val status = HttpStatusCode.BadRequest
        val addUserResult: CallResult<UserResponse> = Result.failure(CallError(status))

        // WHEN
        val response = testRoute(
            request = Request(
                uri = "/user/register",
                method = HttpMethod.Post,
            ),
            testApplicationModule = testApplicationModule(
                userController = userControllerOf(
                    addUserResult = addUserResult,
                )
            )
        )

        // THEN
        assert(response.response.status() == status)
    }

    @Test
    fun `test get user endpoint needs authentication`() {
        // GIVEN
        val expectedStatus = HttpStatusCode.Forbidden

        // WHEN
        val response = testRoute(
            request = Request(
                uri = "/user",
                method = HttpMethod.Get,
            ),
            testApplicationModule = testApplicationModule(
                credentialsValidator = credentialsValidatorOf(subscriptionKeyIsValid = false)
            )
        )

        // THEN
        assert(response.response.status() == expectedStatus)
    }

    @Test
    fun `test that GET user endpoint needs subscription key`() {
        testThatEndpointNeedsSubscriptionKey(
            uri = "/user",
            method = HttpMethod.Get,
        )
    }

    @Test
    fun `test that GET user endpoint needs access token`() {
        testThatEndpointNeedsAccessToken(
            uri = "/user",
            method = HttpMethod.Get,
        )
    }

    @Test
    fun `test get user endpoint returns UserResponse when userController's getUser returns UserResponse and user is authenticated`() {
        // GIVEN
        val userResponse = userResponseOf()

        // WHEN
        val response = testRoute(
            request = Request(
                uri = "/user",
                method = HttpMethod.Get,
                headers = mapOf(
                    "StatsApi-Subscription-Key" to nullUUID().toString(),
                    "StatsApi-Access-Token" to "Access",
                )
            ),
            testApplicationModule = testApplicationModule(
                credentialsValidator = credentialsValidatorOf(
                    subscriptionKeyIsValid = true,
                    accessTokenIsValid = true,
                ),
                userController = userControllerOf(
                    getUserResult = Result.success(userResponse)
                )
            )
        )

        // THEN
        assert(response.response.status()!!.isSuccess())
        assert(response.body<UserResponse>() == userResponse)
    }
}