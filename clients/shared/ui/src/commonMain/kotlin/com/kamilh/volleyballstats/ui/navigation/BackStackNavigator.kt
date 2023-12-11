package com.kamilh.volleyballstats.ui.navigation

import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push

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