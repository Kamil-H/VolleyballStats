package com.kamilh.volleyballstats.clients.app.ui.navigation.node

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.clients.app.ui.screens.filters.PlayerFiltersNode
import com.kamilh.volleyballstats.presentation.features.filter.FiltersPresenter
import com.kamilh.volleyballstats.presentation.navigation.BackStackTarget

class TabNode(
    buildContext: BuildContext,
    private val backStack: BackStack<BackStackTarget>,
    private val appModule: AppModule,
    private val rootNode: (buildContext: BuildContext) -> Node,
) : ParentNode<BackStackTarget>(appyxComponent = backStack, buildContext = buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(
            modifier = modifier.fillMaxSize(),
            appyxComponent = backStack,
        )
    }

    override fun resolve(interactionTarget: BackStackTarget, buildContext: BuildContext): Node =
        when (interactionTarget) {
            is BackStackTarget.PlayerFilters -> PlayerFiltersNode(
                buildContext = buildContext,
                appModule = appModule,
                args = FiltersPresenter.Args(skill = interactionTarget.skill, type = interactionTarget.type),
            )
            BackStackTarget.Root -> rootNode(buildContext)
        }
}
