package com.kamilh.volleyballstats.api

import kotlin.jvm.JvmInline

@JvmInline
value class ApiUrl private constructor(val value: String) {

    companion object {
        val EMPTY: ApiUrl = ApiUrl("")

        val DEBUG: ApiUrl = ApiUrl("dev.volleyballstats.pl")

        val LOCAL: ApiUrl = ApiUrl("10.0.2.2:8080")

        val RELEASE: ApiUrl = ApiUrl("api.volleyballstats.pl")
    }
}
