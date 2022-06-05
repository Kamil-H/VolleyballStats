package com.kamilh.volleyballstats.domain.models

import kotlin.jvm.JvmInline

@JvmInline
value class Season private constructor(val value: Int) {

    operator fun plus(other: Int): Season = Season(value + other)

    companion object {
        val MINIMUM_SUPPORTED = Season(2020)

        fun create(year: Int): Season = Season(year)

        fun createOrNull(year: Int): Season? = if (year.isCorrect()) Season(year) else null

        // TODO: Validate if it's correct year
        private fun Int.isCorrect(): Boolean = true
    }
}