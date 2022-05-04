package com.kamilh.authorization

import com.kamilh.models.AccessToken
import com.kamilh.storage.AccessTokenValidator
import me.tatarka.inject.annotations.Inject

fun interface CredentialsValidator {

    suspend fun isValid(accessToken: AccessToken): Boolean
}

@Inject
class StorageBasedCredentialsValidator(
    private val accessTokenValidator: AccessTokenValidator,
) : CredentialsValidator {

    override suspend fun isValid(accessToken: AccessToken): Boolean = accessTokenValidator.isValid(accessToken)
}