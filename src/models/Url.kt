package com.kamilh.models

import io.ktor.http.*

class Url private constructor(val value: String) {

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