package com.kamilh.authorization

import com.kamilh.models.AccessToken
import com.kamilh.models.Error
import com.kamilh.models.Result
import io.ktor.server.request.*

fun ApplicationRequest.accessTokenHeader(): AccessToken? = call.request.header(ACCESS_TOKEN_HEADER)?.let(::AccessToken)

private const val HEADER_PREFIX = "StatsApi"
private const val ACCESS_TOKEN_HEADER = "$HEADER_PREFIX-Access-Token"

typealias HeaderAuthorizationResult = Result<Unit, HeaderAuthorizationError>

enum class HeaderAuthorizationError(override val message: String) : Error {
    NoAccessToken("\"$ACCESS_TOKEN_HEADER\" header is missing."),
    InvalidAccessToken("Value passed in \"$ACCESS_TOKEN_HEADER\" is not valid.");
}