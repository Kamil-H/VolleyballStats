package com.kamilh.volleyballstats.ui.screens.stats

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.kamilh.volleyballstats.presentation.features.PresenterMap
import com.kamilh.volleyballstats.presentation.features.stats.StatsPresenter
import com.kamilh.volleyballstats.presentation.navigation.Screen
import com.kamilh.volleyballstats.ui.extensions.presenter

class StatsNode(
    buildContext: BuildContext,
    presenterMap: PresenterMap,
    statsScreen: Screen.Stats,
) : Node(buildContext) {

    private val presenter: StatsPresenter = presenter(presenterMap, screen = statsScreen)

    @Composable
    override fun View(modifier: Modifier) {
        StatsScreen(
            modifier = modifier,
            presenter = presenter,
        )
    }
}
