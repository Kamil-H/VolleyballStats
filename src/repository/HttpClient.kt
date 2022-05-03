package com.kamilh.repository

import com.kamilh.models.NetworkError
import com.kamilh.models.NetworkResult
import com.kamilh.models.httprequest.HttpRequest
import com.kamilh.utils.ExceptionLogger
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.tatarka.inject.annotations.Inject
import io.ktor.client.HttpClient as Ktor

interface HttpClient {
    suspend fun <T> execute(httpRequest: HttpRequest<T>): NetworkResult<T>
}

@Inject
class KtorHttpClient(
    private val ktor: Ktor,
    private val exceptionLogger: ExceptionLogger,
) : HttpClient {

    override suspend fun <T> execute(httpRequest: HttpRequest<T>): NetworkResult<T> =
        try {
            val callResult = ktor.request(httpRequest.toHttpRequest())
            if (callResult.status.isSuccess()) {
                val response: T = callResult.body(httpRequest.responseType)
                NetworkResult.success(response)
            } else {
                throw ResponseException(callResult, NO_RESPONSE_TEXT)
            }
        } catch (exception: Exception) {
            exceptionLogger.log(exception)
            NetworkResult.failure(NetworkError.createFrom(exception))
        }
}

private const val NO_RESPONSE_TEXT: String = "<no response text provided>"