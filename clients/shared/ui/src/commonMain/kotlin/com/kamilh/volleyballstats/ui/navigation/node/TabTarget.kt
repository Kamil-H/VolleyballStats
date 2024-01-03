package com.kamilh.volleyballstats.ui.navigation.node

import com.bumble.appyx.components.backstack.BackStack
import com.kamilh.volleyballstats.presentation.navigation.Screen

data class TabTarget(
    val tabScreen: Screen.Tab,
    val backStack: BackStack<Screen.Full>,
)
