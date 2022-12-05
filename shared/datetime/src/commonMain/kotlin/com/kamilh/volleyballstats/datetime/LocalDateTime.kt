package com.kamilh.volleyballstats.datetime

import io.islandtime.*
import io.islandtime.clock.now
import io.islandtime.parser.*
import kotlin.jvm.JvmInline
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

    fun minus(other: LocalDateTime): Duration =
        toInstant().durationSinceUnixEpoch - other.toInstant().durationSinceUnixEpoch

    fun between(other: LocalDateTime): Int =
        io.islandtime.measures.Duration.between(start = this.dateTime, endExclusive = other.dateTime)
            .absoluteValue
            .inDays
            .toInt()

    private fun toInstant(): Instant =
        Instant(dateTime.toInstantAt(UtcOffset.ZERO))

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
