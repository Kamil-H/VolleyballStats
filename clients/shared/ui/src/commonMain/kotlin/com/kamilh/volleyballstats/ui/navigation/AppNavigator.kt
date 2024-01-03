package com.kamilh.volleyballstats.ui.navigation

import com.kamilh.volleyballstats.presentation.navigation.Screen

interface AppNavigator {

    fun goTo(screen: Screen)

    fun pop()
}
