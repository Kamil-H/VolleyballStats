package com.kamilh.volleyballstats.clients.app.ui.navigation.tab

import androidx.core.app.ComponentActivity
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.ui.parallax.BackStackParallax
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.ui.fader.SpotlightFader
import com.bumble.appyx.navigation.modality.BuildContext
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.clients.app.ui.navigation.AppAppyxNavigator
import com.kamilh.volleyballstats.clients.app.ui.navigation.AppyxBackStackNavigator
import com.kamilh.volleyballstats.clients.app.ui.navigation.NavigationEventResolver
import com.kamilh.volleyballstats.clients.app.ui.navigation.node.TabContainerNode
import com.kamilh.volleyballstats.presentation.navigation.BackStackTarget
import com.kamilh.volleyballstats.presentation.navigation.TabTarget
import com.kamilh.volleyballstats.ui.extensions.collectSafely

fun ComponentActivity.TabContainer(
    buildContext: BuildContext,
    appModule: AppModule,
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
    collectSafely(appModule.navigationEventReceiver.receive(), resolver::resolve)
    collectSafely(spotlight.activeIndex) { onTabSelected(tabTargets[it.toInt()]) }
    return TabContainerNode(
        buildContext = buildContext,
        spotlight = spotlight,
        tabDestinations = destinations,
        appModule = appModule,
    )
}
