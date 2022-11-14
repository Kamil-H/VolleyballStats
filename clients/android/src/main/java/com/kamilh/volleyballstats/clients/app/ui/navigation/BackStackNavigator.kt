package com.kamilh.volleyballstats.clients.app.ui.navigation

import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push

interface BackStackNavigator<T : Any> {

    fun push(target: T)

    fun pop()
}

class AppyxBackStackNavigator<T : Any>(
    private val backStack: BackStack<T>,
) : BackStackNavigator<T> {

    override fun push(target: T) {
        backStack.push(target)
    }

    override fun pop() {
        backStack.pop()
    }
}
