package storage

import com.kamilh.authorization.AccessToken

interface AccessTokenValidator {

    suspend fun isValid(accessToken: AccessToken): Boolean
}

class InMemoryAccessTokenValidator : AccessTokenValidator {

    private val accessTokens = listOf("AndroidApp", "iOSApp")

    override suspend fun isValid(accessToken: AccessToken): Boolean = accessTokens.contains(accessToken.value)
}