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
import com.kamilh.volleyballstats.domain.models.stats.StatsType
import com.kamilh.volleyballstats.presentation.features.PresenterMap
import com.kamilh.volleyballstats.presentation.navigation.BackStackTarget
import com.kamilh.volleyballstats.presentation.navigation.TabTarget
import com.kamilh.volleyballstats.ui.screens.home.HomeNode
import com.kamilh.volleyballstats.ui.screens.stats.StatsNode

class TabContainerNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<TabTarget>,
    private val tabDestinations: List<Pair<TabTarget, BackStack<BackStackTarget>>>,
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
        val backStack = getBackStackNavigator(interactionTarget)
        return when (interactionTarget) {
            TabTarget.Home -> tabNode(buildContext, backStack) {
                HomeNode(it, presenterMap)
            }
            TabTarget.PlayersStats -> tabNode(buildContext, backStack) {
                StatsNode(buildContext, presenterMap, statsType = StatsType.Player)
            }
            TabTarget.TeamsStats -> tabNode(buildContext, backStack) {
                StatsNode(buildContext, presenterMap, statsType = StatsType.Team)
            }
        }
    }

    private fun tabNode(
        buildContext: BuildContext,
        backStack: BackStack<BackStackTarget>,
        rootNode: (buildContext: BuildContext) -> Node,
    ): TabNode = TabNode(
        buildContext = buildContext,
        presenterMap = presenterMap,
        backStack = backStack,
        rootNode = rootNode,
    )

    private fun getBackStackNavigator(navTarget: TabTarget): BackStack<BackStackTarget> =
        tabDestinations.find { (tabTarget, _) ->
            tabTarget == navTarget
        }?.second ?: error("BackStackNavigator for target: $navTarget not found.")
}
