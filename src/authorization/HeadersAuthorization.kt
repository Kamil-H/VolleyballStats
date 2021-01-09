package com.kamilh.authorization

import com.kamilh.utils.toUUID
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*

fun Authentication.Configuration.headers(name: String? = null, credentialsValidator: CredentialsValidator) {
    val provider = HeadersAuthorization(
        configuration = HeadersAuthorization.Configuration(name),
        credentialsValidator = credentialsValidator
    )

    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        val subscriptionKey = call.request.subscriptionKeyHeader()
        val accessToken = call.request.accessTokenHeader()

        val result = when {
            subscriptionKey == null -> HeaderAuthorizationResult.NoSubscriptionKey
            accessToken == null -> HeaderAuthorizationResult.NoAccessToken
            else -> provider.validate(subscriptionKey, accessToken)
        }

        val cause = when(result) {
            HeaderAuthorizationResult.NoSubscriptionKey, HeaderAuthorizationResult.NoAccessToken -> AuthenticationFailedCause.NoCredentials
            HeaderAuthorizationResult.InvalidSubscriptionKey, HeaderAuthorizationResult.InvalidAccessToken -> AuthenticationFailedCause.InvalidCredentials
            is HeaderAuthorizationResult.Authorized -> null
        }

        if (cause != null) {
            context.challenge(headersAuthenticationChallengeKey, cause) {
                call.respond(status = HttpStatusCode.Forbidden, result.message)
                it.complete()
            }
        }

        if (result is HeaderAuthorizationResult.Authorized) {
            context.principal(result.headerCredentials)
        }
    }
    register(provider)
}

class HeadersAuthorization(
    configuration: Configuration,
    private val credentialsValidator: CredentialsValidator,
): AuthenticationProvider(configuration) {

    class Configuration(name: String? = null): AuthenticationProvider.Configuration(name)

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
}

private val headersAuthenticationChallengeKey: Any = "HeadersAuth"