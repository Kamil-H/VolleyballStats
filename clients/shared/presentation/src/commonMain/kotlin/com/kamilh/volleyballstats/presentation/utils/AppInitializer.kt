package com.kamilh.volleyballstats.presentation.utils

import com.kamilh.volleyballstats.domain.models.buildinfo.BuildInfo
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildType
import com.kamilh.volleyballstats.domain.utils.Logger
import com.kamilh.volleyballstats.domain.utils.PlatformLogger
import me.tatarka.inject.annotations.Inject

@Inject
class AppInitializer(
    private val buildInfo: BuildInfo,
    private val platformLogger: PlatformLogger,
) {

    fun initialize() {
        if (buildInfo.buildType == BuildType.Debug) {
            Logger.setLogger(platformLogger)
        }
    }
}
