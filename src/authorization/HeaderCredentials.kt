package com.kamilh.authorization

import io.ktor.server.auth.*
import java.util.*

inline class SubscriptionKey(val value: UUID)
inline class AccessToken(val value: String)

data class HeaderCredentials(
    val subscriptionKey: SubscriptionKey,
    val accessToken: AccessToken,
) : Principal