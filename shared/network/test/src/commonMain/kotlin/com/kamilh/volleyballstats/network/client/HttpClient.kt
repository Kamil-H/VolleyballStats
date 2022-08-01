package com.kamilh.volleyballstats.network.client

import com.kamilh.volleyballstats.domain.models.Result
import com.kamilh.volleyballstats.network.HttpClient
import com.kamilh.volleyballstats.network.NetworkResult
import com.kamilh.volleyballstats.network.httprequest.HttpRequest

fun <T> httpClientOf(networkResult: NetworkResult<T>): HttpClient = object : HttpClient {
    override suspend fun <E> execute(httpRequest: HttpRequest<E>): NetworkResult<E> =
        @Suppress("UNCHECKED_CAST")
        when (networkResult) {
            is Result.Failure -> networkResult
            is Result.Success -> (networkResult.value as? E)?.let {
                NetworkResult.success(it)
            } ?: throw Exception()
        }
}