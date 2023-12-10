package com.kamilh.volleyballstats.ui.navigation.tab

import com.kamilh.volleyballstats.ui.navigation.BackStackNavigator
import com.kamilh.volleyballstats.presentation.navigation.BackStackTarget
import com.kamilh.volleyballstats.presentation.navigation.TabTarget

class TabDestination(
    val tabTarget: TabTarget,
    val backStackNavigator: BackStackNavigator<BackStackTarget>,
)
