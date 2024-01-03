package com.kamilh.volleyballstats.ui.navigation

import com.kamilh.volleyballstats.presentation.navigation.NavigationEvent

class NavigationEventResolver(
    private val appNavigator: AppNavigator
) {

    fun resolve(navigationEvent: NavigationEvent) {
        appNavigator.resolveInternal(navigationEvent)
    }
}

private fun AppNavigator.resolveInternal(navigationEvent: NavigationEvent) {
    when (navigationEvent) {
        NavigationEvent.Close -> pop()
        is NavigationEvent.GoTo -> goTo(navigationEvent.screen)
    }
}
