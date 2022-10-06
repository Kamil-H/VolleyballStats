package com.kamilh.volleyballstats.presentation.utils

import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildInfo
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildType
import com.kamilh.volleyballstats.domain.utils.Logger
import com.kamilh.volleyballstats.domain.utils.PlatformLogger
import com.kamilh.volleyballstats.interactors.Synchronizer
import com.kamilh.volleyballstats.presentation.interactors.InitializeFilters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class AppInitializer(
    private val coroutineScope: CoroutineScope,
    private val buildInfo: BuildInfo,
    private val platformLogger: PlatformLogger,
    private val initializeFilters: InitializeFilters,
    private val synchronizer: Synchronizer,
) {

    fun initialize() {
        if (buildInfo.buildType == BuildType.Debug) {
            Logger.setLogger(platformLogger)
        }
        coroutineScope.launch {
            synchronizer.synchronize(League.POLISH_LEAGUE)
            initializeFilters()
        }
    }
}
