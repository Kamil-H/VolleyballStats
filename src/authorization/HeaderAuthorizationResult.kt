package com.kamilh.authorization

import io.ktor.server.request.*

fun ApplicationRequest.subscriptionKeyHeader(): String? = call.request.header(SUBSCRIPTION_KEY_HEADER)

fun ApplicationRequest.accessTokenHeader(): String? = call.request.header(ACCESS_TOKEN_HEADER)

private const val HEADER_PREFIX = "StatsApi"
private const val SUBSCRIPTION_KEY_HEADER = "$HEADER_PREFIX-Subscription-Key"
private const val ACCESS_TOKEN_HEADER = "$HEADER_PREFIX-Access-Token"

sealed class HeaderAuthorizationResult(val message: String) {

    object NoSubscriptionKey: HeaderAuthorizationResult(
        "\"$SUBSCRIPTION_KEY_HEADER\" header is missing."
    )

    object NoAccessToken: HeaderAuthorizationResult(
        "\"$ACCESS_TOKEN_HEADER\" header is missing."
    )

    object InvalidSubscriptionKey: HeaderAuthorizationResult(
        "Value passed in \"$SUBSCRIPTION_KEY_HEADER\" is not valid."
    )

    object InvalidAccessToken: HeaderAuthorizationResult(
        "Value passed in \"$ACCESS_TOKEN_HEADER\" is not valid."
    )

    data class Authorized(val headerCredentials: HeaderCredentials): HeaderAuthorizationResult("OK")
}