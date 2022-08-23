package com.kamilh.volleyballstats.presentation.di

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

actual fun HttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(OkHttp, block)
