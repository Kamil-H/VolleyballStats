package com.kamilh.volleyballstats.models

@JvmInline
value class Country(val code: String) {

    companion object {
        val POLAND: Country get() = Country("PL")
    }
}