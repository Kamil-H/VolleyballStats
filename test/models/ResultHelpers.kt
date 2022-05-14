package com.kamilh.models

import com.kamilh.repository.parsing.ParseError
import com.kamilh.repository.parsing.ParseResult

fun <T, E: Error> successOf(t: T): Result<T, E> = Result.success(t)
fun <T, E: Error> failureOf(e: E): Result<T, E> = Result.failure(e)

fun <T> networkSuccessOf(t: T): NetworkResult<T> = successOf(t)
fun <T> networkFailureOf(networkError: NetworkError): NetworkResult<T> = failureOf(networkError)

fun <T> parseSuccessOf(t: T): ParseResult<T> = successOf(t)
fun <T> parseFailureOf(parseError: ParseError): ParseResult<T> = failureOf(parseError)

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