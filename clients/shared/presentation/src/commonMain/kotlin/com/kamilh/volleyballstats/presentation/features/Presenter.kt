package com.kamilh.volleyballstats.presentation.features

import com.kamilh.volleyballstats.presentation.navigation.Screen
import kotlinx.coroutines.CoroutineScope

class SavableMap(val map: MutableMap<String, Any?>) : Map<String, Any?> by map

fun savableMapOf(): SavableMap = SavableMap(mutableMapOf())

interface Presenter {

    interface Factory<T : Presenter, E : Screen> {

        fun create(
            coroutineScope: CoroutineScope,
            savableMap: SavableMap,
            screen: E,
        ): T
    }
}
