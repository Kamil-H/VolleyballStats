package com.kamilh.volleyballstats.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SafeMap<KEY, VALUE> {
    private val map = mutableMapOf<KEY, VALUE>()
    private val mutex = Mutex()

    suspend fun <T> access(action: suspend (MutableMap<KEY, VALUE>) -> T): T = mutex.withLock {
        action(map)
    }
}

fun <KEY, VALUE> safeMapOf(): SafeMap<KEY, VALUE> = SafeMap()
