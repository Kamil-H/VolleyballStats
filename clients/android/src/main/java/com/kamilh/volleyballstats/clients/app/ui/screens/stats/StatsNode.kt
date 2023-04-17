package com.kamilh.volleyballstats.clients.app.ui.screens.stats

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.clients.app.ui.presenter
import com.kamilh.volleyballstats.domain.models.stats.StatsType
import com.kamilh.volleyballstats.presentation.features.stats.StatsPresenter
import com.kamilh.volleyballstats.ui.screens.stats.StatsScreen

class StatsNode(
    buildContext: BuildContext,
    appModule: AppModule,
    statsType: StatsType,
) : Node(buildContext) {

    private val presenter: StatsPresenter = presenter(appModule.presenterMap, extras = statsType)

    @Composable
    override fun View(modifier: Modifier) {
        StatsScreen(
            modifier = modifier,
            presenter = presenter,
        )
    }
}
