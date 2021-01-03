package com.kamilh.authorization

import storage.AccessTokenValidator
import storage.SubscriptionKeyStorage

interface CredentialsValidator {

    suspend fun isValid(subscriptionKey: SubscriptionKey): Boolean

    suspend fun isValid(accessToken: AccessToken): Boolean
}

class StorageBasedCredentialsValidator(
    private val subscriptionKeyStorage: SubscriptionKeyStorage,
    private val accessTokenValidator: AccessTokenValidator,
) : CredentialsValidator {

    override suspend fun isValid(subscriptionKey: SubscriptionKey): Boolean = subscriptionKeyStorage.contains(subscriptionKey)

    override suspend fun isValid(accessToken: AccessToken): Boolean = accessTokenValidator.isValid(accessToken)
}