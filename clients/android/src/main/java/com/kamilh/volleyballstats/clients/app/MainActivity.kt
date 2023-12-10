package com.kamilh.volleyballstats.clients.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.view.WindowCompat
import com.bumble.appyx.navigation.integration.NodeComponentActivity
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.clients.app.ui.navigation.tab.TabContainer
import com.kamilh.volleyballstats.presentation.features.main.MainPresenter
import com.kamilh.volleyballstats.ui.components.App
import com.kamilh.volleyballstats.ui.extensions.rememberPresenter

class MainActivity : NodeComponentActivity() {

    private val appModule by lazy { AppModule.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This will lay out our app behind the system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val mainPresenter: MainPresenter = appModule.rememberPresenter()
            App(mainPresenter = mainPresenter) {
                NodeHost(
                    lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                    integrationPoint = appyxV2IntegrationPoint,
                ) { buildContext ->
                    TabContainer(buildContext, appModule) {
                        mainPresenter.onTabShown(it)
                    }
                }
            }
        }
    }
}
