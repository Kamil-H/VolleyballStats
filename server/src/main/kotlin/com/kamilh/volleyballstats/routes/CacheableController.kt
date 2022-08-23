package com.kamilh.volleyballstats.routes

import com.kamilh.volleyballstats.utils.SafeMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

interface CacheableController {

    val scope: CoroutineScope

    suspend fun <T> Flow<T>.cache(): StateFlow<T> =
        stateIn(scope = scope)

    suspend fun <KEY, VALUE> SafeMap<KEY, StateFlow<VALUE>>.getCachedFlow(
        key: KEY,
        flowGetter: suspend (KEY) -> Flow<VALUE>,
    ): StateFlow<VALUE> =
        access { map ->
            map[key] ?: flowGetter(key).cache().apply {
                map[key] = this
            }
        }
}
