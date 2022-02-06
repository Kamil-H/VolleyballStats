package com.kamilh.models

@JvmInline
value class TourYear private constructor(val value: Int) {

    operator fun plus(other: Int): TourYear = TourYear(value + other)

    companion object {
        val MINIMUM_SUPPORTED = TourYear(2020)

        fun create(year: Int): TourYear = TourYear(year)

        fun createOrNull(year: Int): TourYear? = if (year.isCorrect()) TourYear(year) else null

        // TODO: Validate if it's correct year
        private fun Int.isCorrect(): Boolean = true
    }
}