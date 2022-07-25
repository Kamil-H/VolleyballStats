package com.kamilh.volleyballstats.extensions

import com.kamilh.volleyballstats.models.DividedList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

fun <T> List<T>.divideExcluding(pivotIndex: Int): DividedList<T> {
    if (pivotIndex < 0 || (lastIndex in 1 until pivotIndex)) {
        throw IndexOutOfBoundsException("Pivot index out of bounds: $pivotIndex, list size: $size")
    }
    return when (pivotIndex) {
        0 -> DividedList(
            before = emptyList(),
            after = drop(1),
        )
        lastIndex -> DividedList(
            before = dropLast(1),
            after = emptyList(),
        )
        else -> DividedList(
            before = subList(0, pivotIndex),
            after = subList(pivotIndex + 1, lastIndex + 1)
        )
    }
}

suspend inline fun <T, R> List<T>.mapAsync(scope: CoroutineScope, crossinline transform: suspend (T) -> R): List<R> =
    map { scope.async { transform(it) } }.awaitAll()
