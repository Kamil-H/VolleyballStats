package com.kamilh.volleyballstats.ui.di

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildInfo
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildType
import com.kamilh.volleyballstats.presentation.di.PresentationModule
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventReceiver
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@Singleton
abstract class AppModule : PresentationModule {

    abstract val navigationEventReceiver: NavigationEventReceiver

    @Provides
    fun buildInfo(): BuildInfo =
        BuildInfo(
            buildType = BuildType.Debug,
            versionName = "0.0.1"
        )

    companion object {
        val instance: AppModule = AppModule::class.create()
    }
}
