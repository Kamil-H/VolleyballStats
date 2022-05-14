package com.kamilh.volleyballstats.authorization

import com.kamilh.volleyballstats.models.AccessToken
import com.kamilh.volleyballstats.models.onFailure
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

fun AuthenticationConfig.headers(name: String? = null, accessTokenValidator: AccessTokenValidator) {
    register(
        HeadersAuthorization(
            configuration = HeadersAuthorization.Configuration(name),
            accessTokenValidator = accessTokenValidator,
        )
    )
}

class HeadersAuthorization(
    configuration: Configuration,
    private val accessTokenValidator: AccessTokenValidator,
) : AuthenticationProvider(configuration) {

    class Configuration(name: String? = null) : Config(name)

    private suspend fun validate(accessToken: AccessToken): HeaderAuthorizationResult =
        when {
            !accessTokenValidator.isValid(accessToken) -> HeaderAuthorizationResult.failure(HeaderAuthorizationError.InvalidAccessToken)
            else -> HeaderAuthorizationResult.success(Unit)
        }

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val call = context.call

        when (val accessToken = call.request.accessTokenHeader()) {
            null -> HeaderAuthorizationResult.failure(HeaderAuthorizationError.NoAccessToken)
            else -> validate(accessToken)
        }.onFailure {
            val cause = when (it) {
                HeaderAuthorizationError.NoAccessToken -> AuthenticationFailedCause.NoCredentials
                HeaderAuthorizationError.InvalidAccessToken -> AuthenticationFailedCause.InvalidCredentials
            }
            context.challenge(headersAuthenticationChallengeKey, cause) { challenge, call ->
                call.respond(status = HttpStatusCode.Forbidden, it.message)
                challenge.complete()
            }
        }
    }
}

private val headersAuthenticationChallengeKey: Any = "HeadersAuth"