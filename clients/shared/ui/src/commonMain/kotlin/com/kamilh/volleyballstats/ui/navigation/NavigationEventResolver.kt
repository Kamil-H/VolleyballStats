package com.kamilh.volleyballstats.ui.navigation

import com.kamilh.volleyballstats.presentation.navigation.BackStackTarget
import com.kamilh.volleyballstats.presentation.navigation.NavigationEvent
import com.kamilh.volleyballstats.presentation.navigation.TabTarget

class NavigationEventResolver(
    private val appNavigator: AppNavigator<TabTarget, BackStackTarget>
) {

    fun resolve(navigationEvent: NavigationEvent) {
        appNavigator.resolveInternal(navigationEvent)
    }
}

private fun AppNavigator<TabTarget, BackStackTarget>.resolveInternal(navigationEvent: NavigationEvent) {
    when (navigationEvent) {
        NavigationEvent.Close -> pop()
        NavigationEvent.HomeTabRequested -> switchTab(TabTarget.Home)
        is NavigationEvent.PlayerFiltersRequested -> push(BackStackTarget.PlayerFilters(navigationEvent.skill, navigationEvent.type))
        NavigationEvent.PlayersTabRequested -> switchTab(TabTarget.PlayersStats)
        NavigationEvent.TeamsTabRequested -> switchTab(TabTarget.TeamsStats)
    }
}