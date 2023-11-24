package com.volleyballstats.ui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildInfo
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildType
import com.kamilh.volleyballstats.presentation.di.PresentationModule
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventReceiver
import com.kamilh.volleyballstats.ui.extensions.rememberPresenter
import com.kamilh.volleyballstats.ui.screens.home.HomeScreen
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import platform.UIKit.UIViewController

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

@Suppress("FunctionName", "unused")
fun MainViewController(): UIViewController =
    ComposeUIViewController {
        val appModule = remember { AppModule.instance }
        LaunchedEffect(appModule) {
            appModule.appInitializer.initialize()
        }
        HomeScreen(presenter = appModule.rememberPresenter())
    }
