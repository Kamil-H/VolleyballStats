package com.kamilh.volleyballstats.presentation.di

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildInfo
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildType
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.presentation.features.create
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventReceiver
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@Singleton
abstract class AppModule : PresentationModule {

    abstract val navigationEventReceiver: NavigationEventReceiver

    abstract val presentersFactory: PresentersFactory

    @Provides
    internal fun presentersFactory(appDispatchers: AppDispatchers): PresentersFactory =
        PresentersFactory(
            appDispatchers = appDispatchers,
            presenterMap = presenterMap,
        )

    @Provides
    internal fun buildInfo(): BuildInfo =
        BuildInfo(
            buildType = BuildType.Debug,
            versionName = "0.0.1"
        )

    companion object {
        val instance: AppModule = AppModule::class.create()
    }
}
