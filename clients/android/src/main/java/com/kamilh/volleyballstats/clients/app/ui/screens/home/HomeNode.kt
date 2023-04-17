package com.kamilh.volleyballstats.clients.app.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.clients.app.ui.presenter
import com.kamilh.volleyballstats.presentation.features.home.HomePresenter
import com.kamilh.volleyballstats.ui.screens.home.HomeScreen

class HomeNode(
    buildContext: BuildContext,
    appModule: AppModule,
) : Node(buildContext) {

    private val presenter: HomePresenter = presenter(appModule.presenterMap)

    @Composable
    override fun View(modifier: Modifier) {
        HomeScreen(
            modifier = modifier,
            presenter = presenter,
        )
    }
}
