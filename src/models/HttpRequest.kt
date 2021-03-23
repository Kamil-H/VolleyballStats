package com.kamilh.models

import io.ktor.client.request.*
import io.ktor.content.*
import io.ktor.http.*

sealed class HttpRequest

data class Endpoint(
    val baseUrl: String,
    val protocol: URLProtocol,
    val path: String,
    val method: HttpMethod,
    val body: Body? = null,
    val queryParams: Map<String, Any?> = emptyMap(),
) : HttpRequest() {
    sealed class Body {
        data class Json(val body: Any): Body()
        data class Plain(val text: String): Body()
    }
}

inline fun Endpoint.toHttpRequest(): HttpRequestBuilder =
    HttpRequestBuilder {
        protocol = this@toHttpRequest.protocol
        host = baseUrl
        encodedPath = this@toHttpRequest.path
    }.apply {
        method = this@toHttpRequest.method
        queryParams.forEach { parameter(it.key, it.value) }
        this@toHttpRequest.body?.let {
            body = when (it) {
                is Endpoint.Body.Json -> {
                    headers.append(HttpHeaders.ContentType, ContentType.Application.Json)
                    it.body
                }
                is Endpoint.Body.Plain -> TextContent(
                    text = it.text,
                    contentType = ContentType.Text.Plain
                )
            }
        }
    }

class Url private constructor(val value: String) : HttpRequest() {

    companion object {
        fun create(urlString: String): Url = Url(io.ktor.http.Url(urlString).toString())

        fun createOrNull(urlString: String): Url? =
            try {
                create(urlString)
            } catch (_: URLParserException) {
                null
            }
    }
}