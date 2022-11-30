package com.kamilh.volleyballstats.clients.app.ui

import androidx.core.app.ComponentActivity
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.clients.app.ui.navigation.*
import com.kamilh.volleyballstats.clients.app.ui.navigation.node.TabContainerNode
import com.kamilh.volleyballstats.presentation.navigation.BackStackTarget
import com.kamilh.volleyballstats.presentation.navigation.TabTarget
import com.kamilh.volleyballstats.ui.extensions.collectSafely

fun ComponentActivity.TabContainer(
    buildContext: BuildContext,
    appModule: AppModule,
): TabContainerNode {
    val backStack: () -> BackStack<BackStackTarget> = {
        BackStack(
            initialElement = BackStackTarget.Root,
            savedStateMap = buildContext.savedStateMap,
        )
    }
    val tabTargets = TabTarget.values()
    val destinations = tabTargets.map { it to backStack() }
    val spotlight = Spotlight(
        items = destinations.map { it.first },
        savedStateMap = buildContext.savedStateMap,
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
    return TabContainerNode(
        buildContext = buildContext,
        spotlight = spotlight,
        tabDestinations = destinations,
        appModule = appModule,
    )
}
