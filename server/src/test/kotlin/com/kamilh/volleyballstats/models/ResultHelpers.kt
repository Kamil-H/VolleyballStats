package com.kamilh.volleyballstats.models

import com.kamilh.volleyballstats.domain.failureOf
import com.kamilh.volleyballstats.domain.successOf
import com.kamilh.volleyballstats.repository.parsing.ParseError
import com.kamilh.volleyballstats.repository.parsing.ParseResult

fun <T> parseSuccessOf(t: T): ParseResult<T> = successOf(t)
fun <T> parseFailureOf(parseError: ParseError): ParseResult<T> = failureOf(parseError)