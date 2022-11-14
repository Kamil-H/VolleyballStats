package com.kamilh.volleyballstats.clients.app.ui.screens.filters

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.ui.extensions.rememberPresenter

class PlayerFiltersNode(
    buildContext: BuildContext,
    private val appModule: AppModule,
    private val skill: StatsSkill,
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        PlayerFiltersScreen(
            modifier = modifier,
            playerFiltersPresenter = appModule.rememberPresenter(skill),
        )
    }
}
