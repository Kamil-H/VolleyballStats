package com.kamilh.volleyballstats.ui.navigation.node

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.kamilh.volleyballstats.presentation.features.PresenterMap
import com.kamilh.volleyballstats.presentation.navigation.Screen
import com.kamilh.volleyballstats.ui.screens.home.HomeNode
import com.kamilh.volleyballstats.ui.screens.stats.StatsNode
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class TabContainerNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<TabTarget>,
    private val presenterMap: PresenterMap,
) : ParentNode<TabTarget>(appyxComponent = spotlight, buildContext = buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(
            modifier = modifier.fillMaxSize(),
            appyxComponent = spotlight,
        )
    }

    override fun resolve(interactionTarget: TabTarget, buildContext: BuildContext): Node {
        val backStack = interactionTarget.backStack
        return when (interactionTarget.tabScreen) {
            Screen.Home -> tabNode(buildContext = buildContext, backStack = backStack) {
                HomeNode(buildContext = it, presenterMap = presenterMap)
            }
            is Screen.Stats -> tabNode(buildContext, backStack) {
                StatsNode(buildContext, presenterMap, interactionTarget.tabScreen)
            }
        }
    }

    private fun tabNode(
        buildContext: BuildContext,
        backStack: BackStack<Screen.Full>,
        rootNode: (buildContext: BuildContext) -> Node,
    ): TabNode = TabNode(
        buildContext = buildContext,
        presenterMap = presenterMap,
        backStack = backStack,
        rootNode = rootNode,
    )
}
