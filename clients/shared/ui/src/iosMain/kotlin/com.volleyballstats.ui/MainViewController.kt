package com.volleyballstats.ui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.ComposeUIViewController
import com.bumble.appyx.navigation.integration.IosNodeHost
import com.bumble.appyx.navigation.integration.MainIntegrationPoint
import com.bumble.appyx.navigation.integration.NodeHost
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildInfo
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildType
import com.kamilh.volleyballstats.presentation.di.PresentationModule
import com.kamilh.volleyballstats.presentation.features.main.MainPresenter
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventReceiver
import com.kamilh.volleyballstats.ui.components.App
import com.kamilh.volleyballstats.ui.extensions.rememberPresenter
import com.kamilh.volleyballstats.ui.navigation.tab.TabContainer
import com.kamilh.volleyballstats.ui.screens.home.HomeScreen
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
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
fun MainViewController(): UIViewController {
    val integrationPoint = MainIntegrationPoint()
    return ComposeUIViewController {
        val scope = rememberCoroutineScope()
        val appModule = remember { AppModule.instance }
        LaunchedEffect(appModule) {
            appModule.appInitializer.initialize()
        }

        val mainPresenter: MainPresenter = appModule.presenterMap.rememberPresenter()
        App(mainPresenter = mainPresenter) {
            IosNodeHost(
                onBackPressedEvents = emptyFlow(),
                integrationPoint = remember { integrationPoint },
            ) { buildContext ->
                TabContainer(
                    coroutineScope = scope,
                    buildContext = buildContext,
                    presenterMap = appModule.presenterMap,
                    navigationEventReceiver = appModule.navigationEventReceiver,
                    onTabSelected = mainPresenter::onTabShown,
                )
            }
        }
    }.also(integrationPoint::setViewController)
}
