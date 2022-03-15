package com.kamilh.utils

import com.kamilh.datetime.*

val testInstant = Instant.parse("2007-12-03T10:15:30.00Z")

fun clockOf(
    instant: Instant = testInstant,
): Clock = Clock.fixedUtc(instant)

val testClock: Clock = clockOf()

fun zonedDateTime(): ZonedDateTime = ZonedDateTime.now(testClock)

fun localDateTime(): LocalDateTime = LocalDateTime.now(testClock)

fun localDate(): LocalDate = LocalDate.now(testClock)