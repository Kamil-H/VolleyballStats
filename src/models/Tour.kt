package com.kamilh.models

@JvmInline
value class Tour private constructor(val value: Int) {

    companion object {
        fun create(year: Int): Tour = Tour(year)

        fun createOrNull(year: Int): Tour? = if (year.isCorrect()) Tour(year) else null

        // TODO: Validate if it's correct year
        private fun Int.isCorrect(): Boolean = true
    }
}