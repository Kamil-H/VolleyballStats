package com.kamilh.models.httprequest

import com.kamilh.models.Url
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.reflect.*

data class UrlRequest<T>(
    val url: Url,
    override val method: HttpMethod = HttpMethod.Get,
    override val body: Body? = null,
    override val responseType: TypeInfo,
) : HttpRequest<T>() {

    companion object {
        inline fun <reified T> create(
            url: Url,
            method: HttpMethod = HttpMethod.Get,
            body: Body? = null,
        ): UrlRequest<T> = UrlRequest(url, method, body, typeInfo<T>())

        fun getHtml(url: Url): UrlRequest<String> = UrlRequest(url, HttpMethod.Get, null, typeInfo<String>())
    }

    override fun toHttpRequest(): HttpRequestBuilder =
        HttpRequestBuilder().apply {
            url(URLBuilder(this@UrlRequest.url.value).build())
            method = this@UrlRequest.method
            this@UrlRequest.body?.let {
                setBody(it.toRequestBody(this))
            }
        }
}