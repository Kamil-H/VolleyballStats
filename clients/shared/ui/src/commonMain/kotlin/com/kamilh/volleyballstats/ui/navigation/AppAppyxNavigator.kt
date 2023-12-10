package com.kamilh.volleyballstats.ui.navigation

import com.kamilh.volleyballstats.presentation.navigation.BackStackTarget
import com.kamilh.volleyballstats.presentation.navigation.TabTarget
import com.kamilh.volleyballstats.ui.navigation.tab.TabDestination
import com.kamilh.volleyballstats.ui.navigation.tab.TabNavigator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AppAppyxNavigator(
    private val targets: List<TabDestination>,
    private val tabNavigator: TabNavigator<TabTarget>,
) : AppNavigator<TabTarget, BackStackTarget> {

    override fun switchTab(target: TabTarget) {
        tabNavigator.switchTab(target)
    }

    override fun push(target: BackStackTarget) {
        getBackStackNavigator().push(target)
    }

    override fun pop() {
        getBackStackNavigator().pop()
    }

    private fun getBackStackNavigator(): BackStackNavigator<BackStackTarget> =
        runBlocking {
            targets.getBackStackNavigator(tabNavigator.activeIndex.first())
        }

    private fun List<TabDestination>.getBackStackNavigator(navTarget: TabTarget): BackStackNavigator<BackStackTarget> =
        find { destination ->
            destination.tabTarget == navTarget
        }?.backStackNavigator ?: error("BackStackNavigator for target: $navTarget not found.")
}
