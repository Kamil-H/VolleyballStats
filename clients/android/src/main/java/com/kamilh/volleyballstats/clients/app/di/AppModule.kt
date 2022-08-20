package com.kamilh.volleyballstats.clients.app.di

import android.app.Application
import android.content.Context
import com.kamilh.volleyballstats.clients.app.BuildConfig
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildInfo
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildType
import com.kamilh.volleyballstats.presentation.di.PresentationModule
import kotlinx.coroutines.CoroutineScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@Singleton
abstract class AppModule(
    @get:Provides val application: Application,
    @get:Provides val scope: CoroutineScope,
) : PresentationModule {

    @Provides
    fun context(): Context = application

    @Provides
    fun buildInfo(): BuildInfo =
        BuildInfo(
            buildType = when (BuildConfig.BUILD_TYPE) {
                "release" -> BuildType.Release
                "debug" -> BuildType.Debug
                else -> throw IllegalStateException("Wrong build type: ${BuildConfig.BUILD_TYPE}")
            },
            versionName = BuildConfig.VERSION_NAME
        )
}
