package com.kamilh.volleyballstats.datetime

import io.islandtime.DateTime
import io.islandtime.TimeZone
import io.islandtime.at
import io.islandtime.clock.now
import io.islandtime.parser.*
import io.islandtime.toDateTime
import kotlin.time.Duration

@JvmInline
value class LocalDateTime internal constructor(private val dateTime: DateTime) : Comparable<LocalDateTime> {

    fun toIso8601String(): String =
        dateTime.toString()

    fun atPolandZone(): ZonedDateTime =
        ZonedDateTime(dateTime.at(TimeZone(id = POLISH_ZONE_ID)))

    fun plus(duration: Duration): LocalDateTime =
        LocalDateTime(dateTime.plus(duration))

    fun minus(duration: Duration): LocalDateTime =
        LocalDateTime(dateTime.minus(duration))

    override fun compareTo(other: LocalDateTime): Int =
        dateTime.compareTo(other.dateTime)

    override fun toString(): String = dateTime.toString()

    companion object {
        private const val POLISH_ZONE_ID = "Europe/Warsaw"

        fun parse(dateString: String): LocalDateTime =
            LocalDateTime(dateString.toDateTime())

        fun parseOrNull(dateString: String): LocalDateTime? =
            dateString.parseDate(Companion::parse)

        fun now(clock: Clock): LocalDateTime =
            LocalDateTime(DateTime.now(clock = clock.clock))
    }
}

// "dd.MM.yyyy, HH:mm"
private val polishParser = dateTimeParser {
    dayOfMonth()
    +'.'
    monthNumber()
    +'.'
    year()
    +','
    +' '
    childParser(DateTimeParsers.Iso.Extended.TIME)
}

fun LocalDateTime.Companion.parsePolishLeagueDate(dateString: String): LocalDateTime =
    LocalDateTime(dateString.toDateTime(polishParser))