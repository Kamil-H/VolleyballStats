package com.kamilh.volleyballstats.clients.app.ui.navigation.tab

import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.activeIndex
import com.bumble.appyx.navmodel.spotlight.operation.activate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface TabNavigator<T : Any> {

    val activeIndex: Flow<T>

    fun switchTab(target: T)
}

class AppyxTabNavigator<T : Any>(
    private val spotlight: Spotlight<T>,
    private val targets: List<T>,
) : TabNavigator<T> {

    override val activeIndex: Flow<T>
        get() = spotlight.activeIndex().map(targets::get)

    override fun switchTab(target: T) {
        spotlight.activate(targets.indexOf(target))
    }
}
