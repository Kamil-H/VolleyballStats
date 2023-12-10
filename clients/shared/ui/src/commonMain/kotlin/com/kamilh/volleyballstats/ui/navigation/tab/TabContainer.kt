package com.kamilh.volleyballstats.ui.navigation.tab

import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.ui.parallax.BackStackParallax
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.ui.fader.SpotlightFader
import com.bumble.appyx.navigation.modality.BuildContext
import com.kamilh.volleyballstats.presentation.features.PresenterMap
import com.kamilh.volleyballstats.presentation.navigation.BackStackTarget
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventReceiver
import com.kamilh.volleyballstats.presentation.navigation.TabTarget
import com.kamilh.volleyballstats.ui.navigation.AppAppyxNavigator
import com.kamilh.volleyballstats.ui.navigation.AppyxBackStackNavigator
import com.kamilh.volleyballstats.ui.navigation.NavigationEventResolver
import com.kamilh.volleyballstats.ui.navigation.node.TabContainerNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Suppress("FunctionNaming", "LongMethod")
fun TabContainer(
    coroutineScope: CoroutineScope,
    buildContext: BuildContext,
    presenterMap: PresenterMap,
    navigationEventReceiver: NavigationEventReceiver,
    onTabSelected: (TabTarget) -> Unit = {},
): TabContainerNode {
    val backStack: () -> BackStack<BackStackTarget> = {
        BackStack(
            model = BackStackModel(
                initialTarget = BackStackTarget.Root,
                savedStateMap = buildContext.savedStateMap,
            ),
            visualisation = ::BackStackParallax,
            gestureFactory = { BackStackParallax.Gestures(it) },
        )
    }
    val tabTargets = TabTarget.entries
    val destinations = tabTargets.map { it to backStack() }
    val spotlight = Spotlight(
        model = SpotlightModel(
            items = destinations.map { it.first },
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = ::SpotlightFader
    )
    val appNavigator = AppAppyxNavigator(
        targets = destinations.map { (tabTarget, backStack) ->
            TabDestination(
                tabTarget = tabTarget,
                backStackNavigator = AppyxBackStackNavigator(backStack)
            )
        },
        tabNavigator = AppyxTabNavigator(spotlight, tabTargets.toList())
    )
    val resolver = NavigationEventResolver(appNavigator)
    coroutineScope.collectSafely(navigationEventReceiver.receive(), resolver::resolve)
    coroutineScope.collectSafely(spotlight.activeIndex) { onTabSelected(tabTargets[it.toInt()]) }
    return TabContainerNode(
        buildContext = buildContext,
        spotlight = spotlight,
        tabDestinations = destinations,
        presenterMap = presenterMap,
    )
}

private fun <T> CoroutineScope.collectSafely(flow: Flow<T>, collector: (T) -> Unit) {
    flow.onEach(collector).launchIn(this)
}
