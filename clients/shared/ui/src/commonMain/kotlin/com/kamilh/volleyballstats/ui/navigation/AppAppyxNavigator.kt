package com.kamilh.volleyballstats.ui.navigation

import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.operation.activate
import com.kamilh.volleyballstats.presentation.navigation.Screen
import com.kamilh.volleyballstats.ui.navigation.node.TabTarget

class AppAppyxNavigator(private val spotlight: Spotlight<TabTarget>) : AppNavigator {

    override fun goTo(screen: Screen) {
        when (screen) {
            is Screen.Full -> push(screen)
            is Screen.Tab -> switchTab(screen)
            else -> error("Unknown screen type: $screen")
        }
    }

    override fun pop() {
        getBackStackNavigator().pop()
    }

    private fun switchTab(target: Screen.Tab) {
        val index = spotlight.uiModels.value.indexOfFirst { it.element.interactionTarget.tabScreen == target }
        spotlight.activate(index.toFloat())
    }

    private fun push(target: Screen.Full) {
        getBackStackNavigator().push(target)
    }

    private fun getBackStackNavigator(): BackStack<Screen.Full> =
        spotlight.activeElement.value?.backStack ?: error("No active element found.")
}
