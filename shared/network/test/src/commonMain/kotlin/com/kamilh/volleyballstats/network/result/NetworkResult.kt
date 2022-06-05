package com.kamilh.volleyballstats.network.result

import com.kamilh.volleyballstats.network.NetworkError
import com.kamilh.volleyballstats.network.NetworkResult

fun <T> networkSuccessOf(t: T): NetworkResult<T> = NetworkResult.success(t)

fun <T> networkFailureOf(networkError: NetworkError): NetworkResult<T> = NetworkResult.failure(networkError)