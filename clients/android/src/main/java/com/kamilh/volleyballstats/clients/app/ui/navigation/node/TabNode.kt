package com.kamilh.volleyballstats.clients.app.ui.navigation.node

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackSlider
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.clients.app.ui.screens.filters.PlayerFiltersNode
import com.kamilh.volleyballstats.presentation.navigation.BackStackTarget

class TabNode(
    buildContext: BuildContext,
    private val backStack: BackStack<BackStackTarget>,
    private val appModule: AppModule,
    private val rootNode: (buildContext: BuildContext) -> Node,
) : ParentNode<BackStackTarget>(navModel = backStack, buildContext = buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            modifier = modifier.fillMaxSize(),
            navModel = backStack,
            transitionHandler = rememberBackstackSlider(
                transitionSpec = {
                    tween(
                        durationMillis = 350,
                        easing = LinearEasing,
                    )
                },
            ),
        )
    }

    override fun resolve(navTarget: BackStackTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is BackStackTarget.PlayerFilters -> PlayerFiltersNode(buildContext, appModule, navTarget.skill)
            BackStackTarget.Root -> rootNode(buildContext)
        }
}
