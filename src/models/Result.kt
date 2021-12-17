package com.kamilh.models

sealed class Result<out V, out E : Error> {

    data class Success<out V>(val value: V) : Result<V, Nothing>()
    data class Failure<out E : Error>(val error: E) : Result<Nothing, E>()

    companion object {
        fun <V, E : Error> success(value: V): Result<V, E> = Success(value)

        fun <V, E : Error> failure(error: E): Result<V, E> = Failure(error)
    }
}

val <V, E : Error> Result<V, E>.value: V?
    get() = when (this) {
        is Result.Success -> value
        is Result.Failure -> null
    }

val <V, E : Error> Result<V, E>.error: E?
    get() = when (this) {
        is Result.Success -> null
        is Result.Failure -> error
    }

inline fun <V, E : Error> Result<V, E>.onSuccess(f: (V) -> Unit): Result<V, E> =
    when (this) {
        is Result.Success -> {
            f(value)
            this
        }
        is Result.Failure -> this
    }

inline fun <V, E : Error> Result<V, E>.onFailure(f: (E) -> Unit): Result<V, E> =
    when (this) {
        is Result.Success -> this
        is Result.Failure -> {
            f(error)
            this
        }
    }

inline fun <V, E : Error, T> Result<V, E>.map(f: (V) -> T): Result<T, E> =
    when (this) {
        is Result.Success -> Result.success(f(value))
        is Result.Failure -> this
    }

inline fun <V, E : Error, T> Result<V, E>.flatMap(f: (V) -> Result<T, E>): Result<T, E> =
    when (this) {
        is Result.Success -> f(value)
        is Result.Failure -> this
    }

inline fun <V, E1 : Error, E2 : Error> Result<V, E1>.mapError(f: (E1) -> E2): Result<V, E2> =
    when (this) {
        is Result.Success -> Result.success(value)
        is Result.Failure -> Result.failure(f(error))
    }

fun <V, E : Error> List<Result<V, E>>.toResults(): Results<V, E> {
    val successes = mutableListOf<Result.Success<V>>()
    val failures = mutableListOf<Result.Failure<E>>()
    forEach { result ->
        when (result) {
            is Result.Failure -> failures.add(result)
            is Result.Success -> successes.add(result)
        }
    }
    return Results(successes = successes, failures = failures)
}