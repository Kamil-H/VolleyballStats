package com.kamilh.volleyballstats.datetime

import io.islandtime.Time
import io.islandtime.at
import io.islandtime.clock.now
import io.islandtime.toInstant
import io.islandtime.toZonedDateTime
import kotlin.jvm.JvmInline
import kotlin.time.Duration
import io.islandtime.ZonedDateTime as IslandZonedDateTime

@JvmInline
value class ZonedDateTime internal constructor(private val zonedDateTime: IslandZonedDateTime) : Comparable<ZonedDateTime> {

    val hour: Int get() = zonedDateTime.hour

    val minute: Int get() = zonedDateTime.minute

    fun toIso8601String(): String =
        zonedDateTime.toString()

    fun atMidnight(): LocalDateTime =
        LocalDateTime(zonedDateTime.dateTime.date.at(Time.MIDNIGHT))

    fun plus(duration: Duration): ZonedDateTime =
        ZonedDateTime(zonedDateTime.plus(duration))

    fun minus(duration: Duration): ZonedDateTime =
        ZonedDateTime(zonedDateTime.minus(duration))

    fun minus(other: ZonedDateTime): Duration =
        toInstant().durationSinceUnixEpoch - other.toInstant().durationSinceUnixEpoch

    fun toLocalDateTime(): LocalDateTime =
        LocalDateTime(zonedDateTime.dateTime)

    fun toLocalDate(): LocalDate =
        LocalDate(zonedDateTime.date)

    fun timeString(): String =
        zonedDateTime.dateTime.time.toString()

    override fun compareTo(other: ZonedDateTime): Int =
        zonedDateTime.compareTo(other.zonedDateTime)

    override fun toString(): String = zonedDateTime.toString()

    private fun toInstant(): Instant =
        Instant(zonedDateTime.toInstant())

    companion object {
        fun parse(dateString: String): ZonedDateTime =
            ZonedDateTime(dateString.toZonedDateTime())

        fun parseOrNull(dateString: String): ZonedDateTime? =
            dateString.parseDate(Companion::parse)

        fun now(clock: Clock): ZonedDateTime =
            ZonedDateTime(IslandZonedDateTime.now(clock = clock.clock))
    }
}
