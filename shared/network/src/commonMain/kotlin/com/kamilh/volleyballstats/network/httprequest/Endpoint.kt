package com.kamilh.volleyballstats.network.httprequest

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.*

data class Endpoint<T>(
    val baseUrl: String,
    val path: String,
    val protocol: URLProtocol,
    val queryParams: Map<String, Any?> = emptyMap(),
    override val method: HttpMethod = HttpMethod.Get,
    override val body: Body? = null,
    override val responseType: TypeInfo,
) : HttpRequest<T>() {

    companion object {
        inline fun <reified T> create(
            baseUrl: String,
            path: String,
            protocol: URLProtocol = URLProtocol.HTTPS,
            queryParams: Map<String, Any?> = emptyMap(),
            method: HttpMethod = HttpMethod.Get,
            body: Body? = null,
        ): Endpoint<T> = Endpoint(baseUrl, path, protocol, queryParams, method, body, typeInfo<T>())
    }

    override fun toHttpRequest(): HttpRequestBuilder =
        HttpRequestBuilder {
            protocol = this@Endpoint.protocol
            host = baseUrl
            encodedPath = this@Endpoint.path
        }.apply {
            method = this@Endpoint.method
            queryParams.forEach { parameter(it.key, it.value) }
            this@Endpoint.body?.let {
                setBody(it.toRequestBody(this))
            }
        }
}
