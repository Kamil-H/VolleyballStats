package com.kamilh.volleyballstats.utils

import com.kamilh.volleyballstats.datetime.Clock
import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.datetime.ZonedDateTime

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