package com.kamilh.volleyballstats.network

import com.kamilh.volleyballstats.domain.models.Url
import com.kamilh.volleyballstats.network.httprequest.Endpoint
import com.kamilh.volleyballstats.network.httprequest.HttpRequest
import com.kamilh.volleyballstats.network.httprequest.UrlRequest
import io.ktor.http.*

inline fun <reified T> endpointOf(
    baseUrl: String = "",
    path: String = "",
    protocol: URLProtocol = URLProtocol.HTTPS,
    queryParams: Map<String, Any?> = emptyMap(),
    method: HttpMethod = HttpMethod.Get,
    body: HttpRequest.Body? = null,
): Endpoint<T> = Endpoint.create(
    baseUrl = baseUrl,
    protocol = protocol,
    path = path,
    method = method,
    body = body,
    queryParams = queryParams,
)

inline fun <reified T> urlRequestOf(
    url: Url = Url.create("google.com"),
    method: HttpMethod = HttpMethod.Get,
    body: HttpRequest.Body? = null,
): UrlRequest<T> = UrlRequest.create(
    url = url,
    method = method,
    body = body,
)