package com.kamilh.models.api

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val subscriptionKey: String,
    val deviceId: String,
    val createDate: String,
)