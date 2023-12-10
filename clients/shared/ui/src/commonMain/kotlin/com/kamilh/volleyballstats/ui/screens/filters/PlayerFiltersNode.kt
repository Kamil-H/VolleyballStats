package com.kamilh.volleyballstats.ui.screens.filters

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.kamilh.volleyballstats.presentation.features.PresenterMap
import com.kamilh.volleyballstats.presentation.features.filter.FiltersPresenter
import com.kamilh.volleyballstats.ui.extensions.presenter

class PlayerFiltersNode(
    buildContext: BuildContext,
    presenterMap: PresenterMap,
    args: FiltersPresenter.Args,
) : Node(buildContext) {

    private val presenter: FiltersPresenter = presenter(presenterMap, extras = args)

    @Composable
    override fun View(modifier: Modifier) {
        PlayerFiltersScreen(
            modifier = modifier,
            presenter = presenter,
        )
    }
}
