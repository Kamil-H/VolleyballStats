package com.kamilh.volleyballstats.domain.models

import kotlin.jvm.JvmInline

@JvmInline
value class Country(val code: String) {

    companion object {
        val POLAND: Country get() = Country("PL")
    }
}
