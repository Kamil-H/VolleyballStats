package com.kamilh.volleyballstats.datetime

import io.islandtime.toInstant
import kotlin.jvm.JvmInline
import kotlin.time.Duration
import io.islandtime.Instant as IslandInstant

@JvmInline
value class Instant internal constructor(internal val instant: IslandInstant) : Comparable<Instant> {

    fun plus(duration: Duration): Instant =
        Instant(instant.plus(duration))

    override fun compareTo(other: Instant): Int =
        instant.compareTo(other.instant)

    override fun toString(): String = instant.toString()

    companion object {
        fun parse(dateString: String): Instant =
            Instant(dateString.toInstant())
    }
}