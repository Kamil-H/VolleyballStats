package com.kamilh.storage

import com.kamilh.models.AccessToken
import me.tatarka.inject.annotations.Inject

interface AccessTokenValidator {

    suspend fun isValid(accessToken: AccessToken): Boolean
}

@Inject
class InMemoryAccessTokenValidator : AccessTokenValidator {

    private val accessTokens = listOf("AndroidApp", "iOSApp")

    override suspend fun isValid(accessToken: AccessToken): Boolean = accessTokens.contains(accessToken.value)
}