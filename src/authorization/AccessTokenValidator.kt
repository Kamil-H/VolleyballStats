package com.kamilh.authorization

import com.kamilh.models.AccessToken
import com.kamilh.models.AppConfig
import me.tatarka.inject.annotations.Inject

interface AccessTokenValidator {

    suspend fun isValid(accessToken: AccessToken): Boolean
}

@Inject
class AppConfigAccessTokenValidator(appConfig: AppConfig) : AccessTokenValidator {

    private val accessTokens = appConfig.accessTokens

    override suspend fun isValid(accessToken: AccessToken): Boolean = accessTokens.contains(accessToken)
}