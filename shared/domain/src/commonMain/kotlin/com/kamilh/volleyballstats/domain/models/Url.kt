package com.kamilh.volleyballstats.domain.models

import io.ktor.http.*
import kotlin.jvm.JvmInline

@JvmInline
value class Url private constructor(val value: String) {

    companion object {
        fun create(urlString: String): Url = Url(io.ktor.http.Url(urlString).toString())

        @Suppress("MandatoryBracesIfStatements")
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
