package com.kamilh.volleyballstats.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.kamilh.volleyballstats.presentation.features.PresenterMap
import com.kamilh.volleyballstats.presentation.features.home.HomePresenter
import com.kamilh.volleyballstats.presentation.navigation.Screen
import com.kamilh.volleyballstats.ui.extensions.presenter
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class HomeNode(
    buildContext: BuildContext,
    presenterMap: PresenterMap,
) : Node(buildContext) {

    private val presenter: HomePresenter = presenter(presenterMap, screen = Screen.Home)

    @Composable
    override fun View(modifier: Modifier) {
        HomeScreen(
            modifier = modifier,
            presenter = presenter,
        )
    }
}
