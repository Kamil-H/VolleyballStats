package com.kamilh.volleyballstats.clients.app.ui.screens.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.clients.app.ui.presenter
import com.kamilh.volleyballstats.presentation.features.players.PlayerStatsPresenter

class PlayerStatsNode(
    buildContext: BuildContext,
    appModule: AppModule,
) : Node(buildContext) {

    private val presenter: PlayerStatsPresenter = presenter(appModule.presenterMap)

    @Composable
    override fun View(modifier: Modifier) {
        PlayerStatsScreen(
            modifier = modifier,
            presenter = presenter,
        )
    }
}
