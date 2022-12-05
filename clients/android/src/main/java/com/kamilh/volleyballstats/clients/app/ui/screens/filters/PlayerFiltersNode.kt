package com.kamilh.volleyballstats.clients.app.ui.screens.filters

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.clients.app.ui.presenter
import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.presentation.features.filter.PlayerFiltersPresenter

class PlayerFiltersNode(
    buildContext: BuildContext,
    appModule: AppModule,
    skill: StatsSkill,
) : Node(buildContext) {

    private val presenter: PlayerFiltersPresenter = presenter(appModule.presenterMap, extras = skill)

    @Composable
    override fun View(modifier: Modifier) {
        PlayerFiltersScreen(
            modifier = modifier,
            playerFiltersPresenter = presenter,
        )
    }
}