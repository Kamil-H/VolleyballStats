package com.kamilh.volleyballstats.clients.app.ui.screens.filters

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.clients.app.ui.presenter
import com.kamilh.volleyballstats.presentation.features.filter.FiltersPresenter
import com.kamilh.volleyballstats.ui.screens.filters.PlayerFiltersScreen

class PlayerFiltersNode(
    buildContext: BuildContext,
    appModule: AppModule,
    args: FiltersPresenter.Args,
) : Node(buildContext) {

    private val presenter: FiltersPresenter = presenter(appModule.presenterMap, extras = args)

    @Composable
    override fun View(modifier: Modifier) {
        PlayerFiltersScreen(
            modifier = modifier,
            presenter = presenter,
        )
    }
}
