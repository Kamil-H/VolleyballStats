package com.kamilh.repository

import com.kamilh.models.*
import com.kamilh.models.HttpRequest
import com.kamilh.models.Url
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

internal suspend inline fun <reified T> HttpClient.execute(httpRequest: HttpRequest): NetworkResult<T> =
    when (httpRequest) {
        is Endpoint -> wrapIntoResult { request(httpRequest.toHttpRequest()) }
        is Url -> wrapIntoResult { request(URLBuilder(httpRequest.value).build()) }
    }

internal inline fun <reified T> wrapIntoResult(executor: () -> T): NetworkResult<T> =
    try {
        NetworkResult.success(executor())
    } catch (exception: Exception) {
        NetworkResult.failure(NetworkError.createFrom(exception))
    }
