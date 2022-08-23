package com.kamilh.volleyballstats.presentation.di

import io.ktor.client.*

expect fun HttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient
