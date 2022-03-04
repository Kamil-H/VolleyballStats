package com.kamilh.utils

import java.time.*

val testClock: Clock = Clock.fixed(Instant.parse("2007-12-03T10:15:30.00Z"), ZoneId.of("Z"))

fun offsetDateTime(): OffsetDateTime = OffsetDateTime.now(testClock)

fun localDateTime(): LocalDateTime = LocalDateTime.now(testClock)

fun localDate(): LocalDate = LocalDate.now(testClock)