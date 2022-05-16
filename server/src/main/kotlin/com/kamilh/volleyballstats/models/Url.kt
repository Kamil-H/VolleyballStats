package com.kamilh.volleyballstats.models

import io.ktor.http.*

@JvmInline
value class Url private constructor(val value: String) {

    companion object {
        fun create(urlString: String): Url = Url(io.ktor.http.Url(urlString).toString())

        fun createOrNull(urlString: String): Url? =
            if (urlString.isBlank()) {
                null
            } else try {
                create(urlString)
            } catch (_: URLParserException) {
                null
            }
    }
}