package com.kamilh.volleyballstats.models.httprequest

import io.ktor.client.request.*
import io.ktor.content.*
import io.ktor.http.*
import io.ktor.util.reflect.*

sealed class HttpRequest<T> {
    abstract val responseType: TypeInfo
    abstract val method: HttpMethod
    abstract val body: Body?

    abstract fun toHttpRequest(): HttpRequestBuilder

    sealed class Body {
        data class Json(val body: Any): Body()
        data class Plain(val text: String): Body()
    }
}

fun HttpRequest.Body.toRequestBody(requestBuilder: HttpRequestBuilder): Any =
    when (this) {
        is HttpRequest.Body.Json -> {
            requestBuilder.headers.append(HttpHeaders.ContentType, ContentType.Application.Json)
            body
        }
        is HttpRequest.Body.Plain -> TextContent(
            text = text,
            contentType = ContentType.Text.Plain
        )
    }