package com.kamilh.volleyballstats.clients.app.ui.screens.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.ui.extensions.rememberPresenter

class PlayerStatsNode(
    buildContext: BuildContext,
    private val appModule: AppModule,
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        PlayerStatsScreen(
            modifier = modifier,
            playerStatsPresenter = appModule.rememberPresenter(),
        )
    }
}
