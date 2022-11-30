package com.kamilh.volleyballstats.presentation.features

import kotlinx.coroutines.CoroutineScope

class SavableMap(val map: MutableMap<String, Any?>) : Map<String, Any?> by map

fun savableMapOf(): SavableMap = SavableMap(mutableMapOf())

interface Presenter {

    interface Factory<T : Presenter, E : Any> {

        fun create(
            coroutineScope: CoroutineScope,
            savableMap: SavableMap,
            extras: E,
        ): T
    }
}
