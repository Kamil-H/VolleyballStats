package com.kamilh.models

import com.kamilh.authorization.SubscriptionKey
import com.kamilh.datetime.LocalDateTime
import java.util.*

data class User(
    val id: Long,
    val subscriptionKey: SubscriptionKey,
    val deviceId: UUID,
    val createDate: LocalDateTime,
)