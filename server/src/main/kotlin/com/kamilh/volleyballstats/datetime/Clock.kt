package com.kamilh.volleyballstats.datetime

import io.islandtime.TimeZone
import io.islandtime.clock.FixedClock
import io.islandtime.clock.SystemClock
import io.islandtime.clock.Clock as IslandClock

@JvmInline
value class Clock internal constructor(internal val clock: IslandClock) {

    override fun toString(): String = clock.toString()

    companion object {
        fun systemDefault(): Clock =
            Clock(SystemClock())

        fun fixedUtc(instant: Instant): Clock =
            Clock(FixedClock(instant = instant.instant, TimeZone.UTC))
    }
}