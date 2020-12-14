package com.kamilh.authorization

import io.ktor.auth.*

inline class SubscriptionKey(val value: String)
inline class AccessToken(val value: String)

data class HeaderCredentials(
    val subscriptionKey: SubscriptionKey,
    val accessToken: AccessToken,
) : Principal