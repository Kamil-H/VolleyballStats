package com.kamilh.repository

import com.kamilh.models.NetworkError
import com.kamilh.models.NetworkResult
import com.kamilh.models.httprequest.HttpRequest
import com.kamilh.utils.ExceptionLogger
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.HttpClient as Ktor

interface HttpClient {
    suspend fun <T> execute(httpRequest: HttpRequest<T>): NetworkResult<T>
}

class KtorHttpClient(
    private val ktor: Ktor,
    private val exceptionLogger: ExceptionLogger,
): HttpClient {

    override suspend fun <T> execute(httpRequest: HttpRequest<T>): NetworkResult<T> =
        try {
            val response: T = ktor.request<HttpResponse>(httpRequest.toHttpRequest()).call.receive(httpRequest.responseType) as T
            NetworkResult.success(response)
        } catch (exception: Exception) {
            exceptionLogger.log(exception)
            NetworkResult.failure(NetworkError.createFrom(exception))
        }
}