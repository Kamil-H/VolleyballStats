package com.kamilh.volleyballstats.presentation.di

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildInfo
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildType
import com.kamilh.volleyballstats.presentation.features.create
import com.kamilh.volleyballstats.presentation.features.main.MainPresenter
import com.kamilh.volleyballstats.presentation.features.savableMapOf
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventReceiver
import kotlinx.coroutines.MainScope
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

    fun createMainPresenter(): MainPresenter = create(
        presenterMap = presenterMap,
        coroutineScope = MainScope(),
        savableMap = savableMapOf(),
        extras = Unit,
    )

    companion object {
        val instance: AppModule = AppModule::class.create()
    }
}
