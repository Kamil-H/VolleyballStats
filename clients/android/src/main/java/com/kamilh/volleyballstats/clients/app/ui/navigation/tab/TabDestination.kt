package com.kamilh.volleyballstats.clients.app.ui.navigation.tab

import com.kamilh.volleyballstats.clients.app.ui.navigation.BackStackNavigator
import com.kamilh.volleyballstats.presentation.navigation.BackStackTarget
import com.kamilh.volleyballstats.presentation.navigation.TabTarget

class TabDestination(
    val tabTarget: TabTarget,
    val backStackNavigator: BackStackNavigator<BackStackTarget>,
)
