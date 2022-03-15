package com.kamilh.utils

import com.kamilh.datetime.Clock
import com.kamilh.datetime.LocalDate
import com.kamilh.datetime.LocalDateTime
import com.kamilh.datetime.ZonedDateTime

object CurrentDate {

    private var clock: Clock = Clock.systemDefault()

    val localDate: LocalDate
        get() = LocalDate.now(clock)

    val localDateTime: LocalDateTime
        get() = LocalDateTime.now(clock)

    val zonedDateTime: ZonedDateTime
        get() = ZonedDateTime.now(clock)

    /**
     * This should be used only in tests!
     */
    fun changeClock(clock: Clock) {
        this.clock = clock
    }
}