package com.kamilh.volleyballstats.authorization

import com.kamilh.volleyballstats.api.AccessToken
import com.kamilh.volleyballstats.models.AppConfig
import me.tatarka.inject.annotations.Inject

interface AccessTokenValidator {

    suspend fun isValid(accessToken: AccessToken): Boolean
}

@Inject
class AppConfigAccessTokenValidator(appConfig: AppConfig) : AccessTokenValidator {

    private val accessTokens = appConfig.accessTokens

    override suspend fun isValid(accessToken: AccessToken): Boolean = accessTokens.contains(accessToken)
}
