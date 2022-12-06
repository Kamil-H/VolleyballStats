package com.kamilh.volleyballstats.clients.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.NodeComponentActivity
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.clients.app.ui.TabContainer
import com.kamilh.volleyballstats.ui.components.App
import com.kamilh.volleyballstats.ui.extensions.rememberPresenter

class MainActivity : NodeComponentActivity() {

    private val appModule by lazy { AppModule.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This will lay out our app behind the system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            App(mainPresenter = appModule.rememberPresenter()) {
                NodeHost(integrationPoint = appyxIntegrationPoint) { buildContext ->
                    TabContainer(buildContext, appModule)
                }
            }
        }
    }
}
