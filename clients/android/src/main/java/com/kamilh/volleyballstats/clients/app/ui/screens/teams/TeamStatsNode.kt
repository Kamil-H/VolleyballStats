package com.kamilh.volleyballstats.clients.app.ui.screens.teams

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.clients.app.ui.navigation.node.EmptyScreen

class TeamStatsNode(
    buildContext: BuildContext,
    private val appModule: AppModule,
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        EmptyScreen(
            modifier = modifier,
            title = "Teams",
        )
    }
}
