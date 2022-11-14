package com.kamilh.volleyballstats.clients.app.ui.navigation

import com.kamilh.volleyballstats.presentation.navigation.BackStackTarget
import com.kamilh.volleyballstats.presentation.navigation.TabTarget

class TabDestination(
    val tabTarget: TabTarget,
    val backStackNavigator: BackStackNavigator<BackStackTarget>,
)
