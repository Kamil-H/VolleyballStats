package com.kamilh.authorization

interface CredentialsValidator {

    suspend fun isValid(subscriptionKey: SubscriptionKey): Boolean

    suspend fun isValid(accessToken: AccessToken): Boolean
}

class SimpleValidator: CredentialsValidator {

    private val correctAccessTokens = listOf("AndroidApp", "iOSApp")
    private val correctSubscriptionKeys = mutableListOf("1902381293")

    override suspend fun isValid(subscriptionKey: SubscriptionKey): Boolean = correctSubscriptionKeys.contains(subscriptionKey.value)

    override suspend fun isValid(accessToken: AccessToken): Boolean = correctAccessTokens.contains(accessToken.value)

    fun addSubscriptionKey(subscriptionKey: SubscriptionKey) {
        correctSubscriptionKeys.add(subscriptionKey.value)
    }
}