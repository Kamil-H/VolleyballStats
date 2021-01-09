package com.kamilh.authorization

import com.kamilh.storage.nullUUID
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HeadersAuthorizationTest {

    private fun authorization(
        subscriptionKeyIsValid: Boolean = false,
        accessTokenIsValid: Boolean = false,
    ) = HeadersAuthorization(
        configuration = HeadersAuthorization.Configuration(),
        credentialsValidator = credentialsValidatorOf(
            subscriptionKeyIsValid = subscriptionKeyIsValid,
            accessTokenIsValid = accessTokenIsValid
        )
    )

    @Test
    fun `test if returned result is InvalidSubscriptionKey when SubscriptionKey is not valid`() = runBlockingTest {
        // GIVEN
        val subscriptionKeyIsValid = false
        val accessTokenIsValid = true

        // WHEN
        val result = authorization(
            subscriptionKeyIsValid = subscriptionKeyIsValid,
            accessTokenIsValid = accessTokenIsValid,
        ).validate(
            subscriptionKeyString = "",
            accessTokenString = ""
        )

        // THEN
        assertTrue(result is HeaderAuthorizationResult.InvalidSubscriptionKey)
    }

    @Test
    fun `test if returned result is InvalidAccessToken when SubscriptionKey is not valid`() = runBlockingTest {
        // GIVEN
        val subscriptionKeyIsValid = true
        val accessTokenIsValid = false

        // WHEN
        val result = authorization(
            subscriptionKeyIsValid = subscriptionKeyIsValid,
            accessTokenIsValid = accessTokenIsValid,
        ).validate(
            subscriptionKeyString = nullUUID().toString(),
            accessTokenString = ""
        )

        // THEN
        assertTrue(result is HeaderAuthorizationResult.InvalidAccessToken)
    }

    @Test
    fun `test if returned result is Authorized when credentials are valid`() = runBlockingTest {
        // GIVEN
        val subscriptionKeyIsValid = true
        val accessTokenIsValid = true
        val subscriptionKey = nullUUID().toString()
        val accessToken = "accessToken"

        // WHEN
        val result = authorization(
            subscriptionKeyIsValid = subscriptionKeyIsValid,
            accessTokenIsValid = accessTokenIsValid,
        ).validate(
            subscriptionKeyString = subscriptionKey,
            accessTokenString = accessToken
        )

        // THEN
        require(result is HeaderAuthorizationResult.Authorized)
        assertEquals(subscriptionKey, result.headerCredentials.subscriptionKey.value.toString())
        assertEquals(accessToken, result.headerCredentials.accessToken.value)
    }
}

fun credentialsValidatorOf(
    subscriptionKeyIsValid: Boolean = false,
    accessTokenIsValid: Boolean = false,
): CredentialsValidator = object : CredentialsValidator {
    override suspend fun isValid(subscriptionKey: SubscriptionKey): Boolean = subscriptionKeyIsValid
    override suspend fun isValid(accessToken: AccessToken): Boolean = accessTokenIsValid
}