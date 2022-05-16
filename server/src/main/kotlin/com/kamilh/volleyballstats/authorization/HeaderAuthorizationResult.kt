package com.kamilh.volleyballstats.authorization

import com.kamilh.volleyballstats.models.AccessToken
import com.kamilh.volleyballstats.models.Error
import com.kamilh.volleyballstats.models.Result
import io.ktor.server.request.*

fun ApplicationRequest.accessTokenHeader(): AccessToken? = call.request.header(ACCESS_TOKEN_HEADER)?.let(::AccessToken)

private const val HEADER_PREFIX = "StatsApi"
private const val ACCESS_TOKEN_HEADER = "$HEADER_PREFIX-Access-Token"

typealias HeaderAuthorizationResult = Result<Unit, HeaderAuthorizationError>

enum class HeaderAuthorizationError(override val message: String) : Error {
    NoAccessToken("\"$ACCESS_TOKEN_HEADER\" header is missing."),
    InvalidAccessToken("Value passed in \"$ACCESS_TOKEN_HEADER\" is not valid.");
}