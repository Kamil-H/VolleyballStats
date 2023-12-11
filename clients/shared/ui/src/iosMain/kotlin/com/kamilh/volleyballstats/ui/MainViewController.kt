package com.kamilh.volleyballstats.ui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.ComposeUIViewController
import com.bumble.appyx.navigation.integration.IosNodeHost
import com.bumble.appyx.navigation.integration.MainIntegrationPoint
import com.kamilh.volleyballstats.presentation.features.main.MainPresenter
import com.kamilh.volleyballstats.ui.components.App
import com.kamilh.volleyballstats.ui.di.AppModule
import com.kamilh.volleyballstats.ui.extensions.rememberPresenter
import com.kamilh.volleyballstats.ui.navigation.tab.TabContainer
import kotlinx.coroutines.flow.emptyFlow
import platform.UIKit.UIViewController

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
