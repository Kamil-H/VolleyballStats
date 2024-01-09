package com.kamilh.volleyballstats.ui.navigation.tab

import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.ui.parallax.BackStackParallax
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.ui.fader.SpotlightFader
import com.bumble.appyx.navigation.modality.BuildContext
import com.kamilh.volleyballstats.domain.models.stats.StatsType
import com.kamilh.volleyballstats.presentation.features.PresenterMap
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventReceiver
import com.kamilh.volleyballstats.presentation.navigation.Screen
import com.kamilh.volleyballstats.ui.navigation.AppAppyxNavigator
import com.kamilh.volleyballstats.ui.navigation.NavigationEventResolver
import com.kamilh.volleyballstats.ui.navigation.node.TabContainerNode
import com.kamilh.volleyballstats.ui.navigation.node.TabTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
@Suppress("FunctionNaming")
fun TabContainer(
    coroutineScope: CoroutineScope,
    buildContext: BuildContext,
    presenterMap: PresenterMap,
    navigationEventReceiver: NavigationEventReceiver,
    onTabSelected: (Int) -> Unit = {},
): TabContainerNode {
    val tabTargets = listOf(Screen.Home, Screen.Stats(StatsType.Player), Screen.Stats(StatsType.Team)).map {
        it.toTabTarget(buildContext)
    }
    val spotlight = Spotlight(
        model = SpotlightModel(
            items = tabTargets,
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = ::SpotlightFader
    )
    val appNavigator = AppAppyxNavigator(spotlight = spotlight)
    val resolver = NavigationEventResolver(appNavigator)
    coroutineScope.collectSafely(navigationEventReceiver.receive(), resolver::resolve)
    coroutineScope.collectSafely(spotlight.activeIndex) { onTabSelected(it.toInt()) }
    return TabContainerNode(
        buildContext = buildContext,
        spotlight = spotlight,
        presenterMap = presenterMap,
    )
}

private fun Screen.Tab.toTabTarget(buildContext: BuildContext): TabTarget =
    TabTarget(
        tabScreen = this,
        backStack = backStack(buildContext),
    )

private fun backStack(buildContext: BuildContext): BackStack<Screen.Full> =
    BackStack(
        model = BackStackModel(
            initialTarget = Screen.Full.Root,
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = ::BackStackParallax,
        gestureFactory = { BackStackParallax.Gestures(it) },
    )

private fun <T> CoroutineScope.collectSafely(flow: Flow<T>, collector: (T) -> Unit) {
    flow.onEach(collector).launchIn(this)
}
