package com.kamilh.volleyballstats.presentation.utils

import com.kamilh.volleyballstats.api.AccessToken
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildInfo
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildType
import me.tatarka.inject.annotations.Inject

@Inject
class AccessTokenProvider(private val buildInfo: BuildInfo) {

    fun get(): AccessToken =
        when (buildInfo.buildType) {
            BuildType.Debug -> AccessToken(DEBUG_ACCESS_TOKEN)
            BuildType.Release -> AccessToken(DEBUG_ACCESS_TOKEN)
        }

    companion object {
        private const val DEBUG_ACCESS_TOKEN = "application"
    }
}
