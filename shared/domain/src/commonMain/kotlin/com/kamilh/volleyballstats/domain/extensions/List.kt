package com.kamilh.volleyballstats.domain.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

suspend inline fun <T, R> List<T>.mapAsync(scope: CoroutineScope, crossinline transform: suspend (T) -> R): List<R> =
    map { scope.async { transform(it) } }.awaitAll()
