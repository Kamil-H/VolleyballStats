package com.kamilh.volleyballstats.clients.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.navigation.integration.NodeComponentActivity
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.presentation.features.main.MainPresenter
import com.kamilh.volleyballstats.ui.components.App
import com.kamilh.volleyballstats.ui.extensions.rememberPresenter
import com.kamilh.volleyballstats.ui.navigation.tab.TabContainer

class MainActivity : NodeComponentActivity() {

    private val appModule by lazy { AppModule.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This will lay out our app behind the system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val mainPresenter: MainPresenter = appModule.presenterMap.rememberPresenter()
            App(mainPresenter = mainPresenter) {
                NodeHost(
                    lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                    integrationPoint = appyxV2IntegrationPoint,
                ) { buildContext ->
                    TabContainer(
                        coroutineScope = lifecycleScope,
                        buildContext = buildContext,
                        presenterMap = appModule.presenterMap,
                        navigationEventReceiver = appModule.navigationEventReceiver,
                        onTabSelected = mainPresenter::onTabShown,
                    )
                }
            }
        }
    }
}
