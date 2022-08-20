package com.kamilh.volleyballstats.api

import kotlin.jvm.JvmInline

@JvmInline
value class ApiUrl private constructor(val value: String) {

    companion object {
        val EMPTY: ApiUrl = ApiUrl("")

        val DEBUG: ApiUrl = ApiUrl("srv11.mikr.us:20213")

        val RELEASE: ApiUrl = ApiUrl("srv11.mikr.us:30213")
    }
}
