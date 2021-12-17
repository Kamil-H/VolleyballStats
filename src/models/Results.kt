package com.kamilh.models

data class Results<V, E : Error>(
    val successes: List<Result.Success<V>>,
    val failures: List<Result.Failure<E>>,
)

val <V, E : Error> Results<V, E>.firstFailure: Result.Failure<E>?
    get() = failures.firstOrNull()

val <V, E : Error> Results<V, E>.values: List<V>
    get() = successes.map { it.value }

val <V, E : Error> Results<V, E>.errors: List<E>
    get() = failures.map { it.error }