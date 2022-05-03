package com.kamilh.authorization

import com.kamilh.utils.toUUID
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

fun AuthenticationConfig.headers(name: String? = null, credentialsValidator: CredentialsValidator) {
    register(
        HeadersAuthorization(
            configuration = HeadersAuthorization.Configuration(name),
            credentialsValidator = credentialsValidator
        )
    )
}

class HeadersAuthorization(
    configuration: Configuration,
    private val credentialsValidator: CredentialsValidator,
): AuthenticationProvider(configuration) {

    class Configuration(name: String? = null): Config(name)

    suspend fun validate(subscriptionKeyString: String, accessTokenString: String): HeaderAuthorizationResult {
        val subscriptionKey = subscriptionKeyString.toUUID()?.let(::SubscriptionKey)
        val accessToken = AccessToken(accessTokenString)
        return when {
            subscriptionKey == null -> HeaderAuthorizationResult.InvalidSubscriptionKey
            !credentialsValidator.isValid(subscriptionKey) -> HeaderAuthorizationResult.InvalidSubscriptionKey
            !credentialsValidator.isValid(accessToken) -> HeaderAuthorizationResult.InvalidAccessToken
            else -> HeaderAuthorizationResult.Authorized(
                HeaderCredentials(
                    subscriptionKey = subscriptionKey,
                    accessToken = accessToken,
                )
            )
        }
    }

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val call = context.call
        val subscriptionKey = call.request.subscriptionKeyHeader()
        val accessToken = call.request.accessTokenHeader()

        val result = when {
            subscriptionKey == null -> HeaderAuthorizationResult.NoSubscriptionKey
            accessToken == null -> HeaderAuthorizationResult.NoAccessToken
            else -> validate(subscriptionKey, accessToken)
        }

        val cause = when(result) {
            HeaderAuthorizationResult.NoSubscriptionKey, HeaderAuthorizationResult.NoAccessToken -> AuthenticationFailedCause.NoCredentials
            HeaderAuthorizationResult.InvalidSubscriptionKey, HeaderAuthorizationResult.InvalidAccessToken -> AuthenticationFailedCause.InvalidCredentials
            is HeaderAuthorizationResult.Authorized -> null
        }

        if (cause != null) {
            context.challenge(headersAuthenticationChallengeKey, cause) { challenge, call ->
                call.respond(status = HttpStatusCode.Forbidden, result.message)
                challenge.complete()
            }
        }

        if (result is HeaderAuthorizationResult.Authorized) {
            context.principal(result.headerCredentials)
        }
    }
}

private val headersAuthenticationChallengeKey: Any = "HeadersAuth"