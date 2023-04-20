package com.kamilh.volleyballstats.ui.navigation

interface AppNavigator<TAB: Any, BACKSTACK: Any> {

    fun switchTab(target: TAB)

    fun push(target: BACKSTACK)

    fun pop()
}

