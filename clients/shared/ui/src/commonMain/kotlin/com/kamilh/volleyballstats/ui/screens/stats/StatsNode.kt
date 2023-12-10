package com.kamilh.volleyballstats.ui.screens.stats

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.kamilh.volleyballstats.domain.models.stats.StatsType
import com.kamilh.volleyballstats.presentation.features.PresenterMap
import com.kamilh.volleyballstats.presentation.features.stats.StatsPresenter
import com.kamilh.volleyballstats.ui.extensions.presenter

class StatsNode(
    buildContext: BuildContext,
    presenterMap: PresenterMap,
    statsType: StatsType,
) : Node(buildContext) {

    private val presenter: StatsPresenter = presenter(presenterMap, extras = statsType)

    @Composable
    override fun View(modifier: Modifier) {
        StatsScreen(
            modifier = modifier,
            presenter = presenter,
        )
    }
}
