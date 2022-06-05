package com.kamilh.volleyballstats.domain

import com.kamilh.volleyballstats.domain.models.Error
import com.kamilh.volleyballstats.domain.models.Result

fun <T, E: Error> successOf(t: T): Result<T, E> = Result.success(t)
fun <T, E: Error> failureOf(e: E): Result<T, E> = Result.failure(e)

fun <V, E: Error> Result<V, E>.assertSuccess(asserter: (V.() -> Unit)? = null) {
    if (this is Result.Success<V>) {
        asserter?.invoke(this.value)
    } else {
        throw IllegalArgumentException("Expected Success, found: $this")
    }
}

fun <V, E: Error> Result<V, E>.assertFailure(asserter: (E.() -> Unit)? = null) {
    if (this is Result.Failure<E>) {
        asserter?.invoke(this.error)
    } else {
        throw IllegalArgumentException("Expected Failure, found: $this")
    }
}