package com.kamilh.models

import com.kamilh.authorization.SubscriptionKey
import java.time.OffsetDateTime
import java.util.*

data class User(
    val id: Long,
    val subscriptionKey: SubscriptionKey,
    val deviceId: UUID,
    val createDate: OffsetDateTime,
)