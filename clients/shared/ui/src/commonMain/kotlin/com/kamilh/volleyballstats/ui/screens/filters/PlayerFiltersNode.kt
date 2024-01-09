package com.kamilh.volleyballstats.ui.screens.filters

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.kamilh.volleyballstats.presentation.features.PresenterMap
import com.kamilh.volleyballstats.presentation.features.filter.FiltersPresenter
import com.kamilh.volleyballstats.presentation.navigation.Screen
import com.kamilh.volleyballstats.ui.extensions.presenter
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class PlayerFiltersNode(
    buildContext: BuildContext,
    presenterMap: PresenterMap,
    filters: Screen.Filters,
) : Node(buildContext) {

    private val presenter: FiltersPresenter = presenter(presenterMap, screen = filters)

    @Composable
    override fun View(modifier: Modifier) {
        PlayerFiltersScreen(
            modifier = modifier,
            presenter = presenter,
        )
    }
}
