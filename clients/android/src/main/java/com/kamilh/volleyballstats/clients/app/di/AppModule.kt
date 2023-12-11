package com.kamilh.volleyballstats.clients.app.di

import android.app.Application
import android.content.Context
import com.kamilh.volleyballstats.clients.app.BuildConfig
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildInfo
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildType
import com.kamilh.volleyballstats.presentation.di.PresentationModule
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventReceiver
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@Singleton
abstract class AppModule(
    @get:Provides val application: Application,
) : PresentationModule {

    abstract val navigationEventReceiver: NavigationEventReceiver

    @Provides
    fun context(): Context = application

    @Provides
    fun buildInfo(): BuildInfo =
        BuildInfo(
            buildType = when (BuildConfig.BUILD_TYPE) {
                "release" -> BuildType.Release
                "debug" -> BuildType.Debug
                "local" -> BuildType.Local
                else -> error("Wrong build type: ${BuildConfig.BUILD_TYPE}")
            },
            versionName = BuildConfig.VERSION_NAME
        )

    companion object {
        private var instance: AppModule? = null

        fun getInstance(context: Context) = instance ?: AppModule::class.create(
            context.applicationContext as Application,
        ).also { instance = it }
    }
}
