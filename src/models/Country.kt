package com.kamilh.models

@JvmInline
value class Country(val code: String) {

    companion object {
        val POLAND: Country get() = Country("PL")
    }
}