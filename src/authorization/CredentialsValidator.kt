package com.kamilh.authorization

import com.kamilh.interactors.SubscriptionKeyValidator
import com.kamilh.interactors.SubscriptionKeyValidatorParams
import com.kamilh.interactors.SubscriptionKeyValidatorResult
import storage.AccessTokenValidator

interface CredentialsValidator {

    suspend fun isValid(subscriptionKey: SubscriptionKey): Boolean

    suspend fun isValid(accessToken: AccessToken): Boolean
}

class StorageBasedCredentialsValidator(
    private val subscriptionKeyValidator: SubscriptionKeyValidator,
    private val accessTokenValidator: AccessTokenValidator,
) : CredentialsValidator {

    override suspend fun isValid(subscriptionKey: SubscriptionKey): Boolean =
        subscriptionKeyValidator(SubscriptionKeyValidatorParams(subscriptionKey)) == SubscriptionKeyValidatorResult.Valid

    override suspend fun isValid(accessToken: AccessToken): Boolean = accessTokenValidator.isValid(accessToken)
}