package com.kamilh.datetime

import io.islandtime.Date
import io.islandtime.clock.now
import io.islandtime.parser.dateTimeParser
import io.islandtime.parser.dayOfMonth
import io.islandtime.parser.monthNumber
import io.islandtime.parser.year
import io.islandtime.toDate

@JvmInline
value class LocalDate internal constructor(private val date: Date) : Comparable<LocalDate> {

    fun toIso8601String(): String =
        date.toString()

    override fun compareTo(other: LocalDate): Int =
        date.compareTo(other.date)

    companion object {
        fun of(year: Int, month: Int, day: Int): LocalDate =
            LocalDate(Date(year, month, day))

        fun parse(dateString: String): LocalDate =
            LocalDate(dateString.toDate())

        fun parseOrNull(dateString: String): LocalDate? =
            dateString.parseDate(::parse)

        fun now(clock: Clock): LocalDate =
            LocalDate(Date.now(clock = clock.clock))
    }
}

// dd.MM.yyyy
private val polishParser = dateTimeParser {
    dayOfMonth()
    +'.'
    monthNumber()
    +'.'
    year()
}

fun LocalDate.Companion.parsePolishLeagueDate(dateString: String): LocalDate =
    LocalDate(dateString.toDate(polishParser))