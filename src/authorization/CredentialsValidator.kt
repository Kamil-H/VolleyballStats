package com.kamilh.authorization

import com.kamilh.interactors.SubscriptionKeyValidator
import com.kamilh.interactors.SubscriptionKeyValidatorParams
import com.kamilh.interactors.SubscriptionKeyValidatorResult
import com.kamilh.storage.AccessTokenValidator
import me.tatarka.inject.annotations.Inject

interface CredentialsValidator {

    suspend fun isValid(subscriptionKey: SubscriptionKey): Boolean

    suspend fun isValid(accessToken: AccessToken): Boolean
}

@Inject
class StorageBasedCredentialsValidator(
    private val subscriptionKeyValidator: SubscriptionKeyValidator,
    private val accessTokenValidator: AccessTokenValidator,
) : CredentialsValidator {

    override suspend fun isValid(subscriptionKey: SubscriptionKey): Boolean =
        subscriptionKeyValidator(SubscriptionKeyValidatorParams(subscriptionKey)) == SubscriptionKeyValidatorResult.Valid

    override suspend fun isValid(accessToken: AccessToken): Boolean = accessTokenValidator.isValid(accessToken)
}