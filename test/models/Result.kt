package com.kamilh.models

import repository.parsing.ParseError
import repository.parsing.ParseResult

fun <T, E: Error> successOf(t: T): Result<T, E> = Result.success(t)
fun <T, E: Error> failureOf(e: E): Result<T, E> = Result.failure(e)

fun <T> networkSuccessOf(t: T): NetworkResult<T> = successOf(t)
fun <T> networkFailureOf(networkError: NetworkError): NetworkResult<T> = failureOf(networkError)

fun <T> parseSuccessOf(t: T): ParseResult<T> = successOf(t)
fun <T> parseFailureOf(parseError: ParseError): ParseResult<T> = failureOf(parseError)