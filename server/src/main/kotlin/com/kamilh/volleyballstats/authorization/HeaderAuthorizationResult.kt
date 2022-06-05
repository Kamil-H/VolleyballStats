package com.kamilh.volleyballstats.authorization

import com.kamilh.volleyballstats.api.ApiConstants
import com.kamilh.volleyballstats.domain.models.Error
import com.kamilh.volleyballstats.domain.models.Result
import com.kamilh.volleyballstats.models.AccessToken
import io.ktor.server.request.*

fun ApplicationRequest.accessTokenHeader(): AccessToken? =
    call.request.header(ApiConstants.HEADER_ACCESS_TOKEN)?.let(::AccessToken)

typealias HeaderAuthorizationResult = Result<Unit, HeaderAuthorizationError>

enum class HeaderAuthorizationError(override val message: String) : Error {
    NoAccessToken("\"${ApiConstants.HEADER_ACCESS_TOKEN}\" header is missing."),
    InvalidAccessToken("Value passed in \"${ApiConstants.HEADER_ACCESS_TOKEN}\" is not valid.");
}