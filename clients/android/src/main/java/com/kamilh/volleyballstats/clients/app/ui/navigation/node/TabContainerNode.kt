package com.kamilh.volleyballstats.clients.app.ui.navigation.node

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.transitionhandler.rememberSpotlightFader
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.clients.app.ui.screens.home.HomeNode
import com.kamilh.volleyballstats.clients.app.ui.screens.player.PlayerStatsNode
import com.kamilh.volleyballstats.clients.app.ui.screens.teams.TeamStatsNode
import com.kamilh.volleyballstats.presentation.navigation.BackStackTarget
import com.kamilh.volleyballstats.presentation.navigation.TabTarget

class TabContainerNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<TabTarget>,
    private val tabDestinations: List<Pair<TabTarget, BackStack<BackStackTarget>>>,
    private val appModule: AppModule,
) : ParentNode<TabTarget>(navModel = spotlight, buildContext = buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            modifier = modifier.fillMaxSize(),
            navModel = spotlight,
            transitionHandler = rememberSpotlightFader(
                transitionSpec = {
                    tween(
                        durationMillis = 300,
                        easing = LinearEasing,
                    )
                }
            ),
        )
    }

    override fun resolve(navTarget: TabTarget, buildContext: BuildContext): Node {
        val backStack = getBackStackNavigator(navTarget)
        return when (navTarget) {
            TabTarget.Home -> tabNode(buildContext, backStack) {
                HomeNode(it, appModule)
            }
            TabTarget.PlayersStats -> tabNode(buildContext, backStack) {
                PlayerStatsNode(buildContext, appModule)
            }
            TabTarget.TeamsStats -> tabNode(buildContext, backStack) {
                TeamStatsNode(buildContext, appModule)
            }
        }
    }

    private fun tabNode(
        buildContext: BuildContext,
        backStack: BackStack<BackStackTarget>,
        rootNode: (buildContext: BuildContext) -> Node,
    ): TabNode = TabNode(
        buildContext = buildContext,
        appModule = appModule,
        backStack = backStack,
    ) { rootNode(it) }

    private fun getBackStackNavigator(navTarget: TabTarget): BackStack<BackStackTarget> =
        tabDestinations.find { (tabTarget, _) ->
            tabTarget == navTarget
        }?.second ?: error("BackStackNavigator for target: $navTarget not found.")
}

@Composable
fun EmptyScreen(modifier: Modifier = Modifier, title: String) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}
