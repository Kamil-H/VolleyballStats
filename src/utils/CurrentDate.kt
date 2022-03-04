package com.kamilh.utils

import java.time.*

object CurrentDate {

    private var clock: Clock = Clock.systemDefaultZone()

    val localDate: LocalDate
        get() = LocalDate.now(clock)

    val localDateTime: LocalDateTime
        get() = LocalDateTime.now(clock)

    val offsetDateTime: OffsetDateTime
        get() = OffsetDateTime.now(clock)

    val zonedDateTime: ZonedDateTime
        get() = ZonedDateTime.now(clock)

    /**
     * This should be used only in tests!
     */
    fun changeClock(clock: Clock) {
        this.clock = clock
    }
}