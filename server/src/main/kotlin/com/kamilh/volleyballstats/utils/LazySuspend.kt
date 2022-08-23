package com.kamilh.volleyballstats.utils

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.atomic.AtomicReference

class LazySuspend<T>(private val block: suspend () -> T) {

    private val value = AtomicReference<Deferred<T>>()

    suspend operator fun invoke(): T = (
        value.get()
            ?: coroutineScope {
                value.updateAndGet { actual ->
                    actual ?: async { block() }
                }
            }
        ).await()
}
