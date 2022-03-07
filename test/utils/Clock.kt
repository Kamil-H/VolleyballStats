package com.kamilh.utils

import java.time.*

val testInstant = Instant.parse("2007-12-03T10:15:30.00Z")
val testZoneId = ZoneId.of("Z")

fun clockOf(
    instant: Instant = testInstant,
    zoneId: ZoneId = testZoneId,
): Clock = Clock.fixed(instant, zoneId)

val testClock: Clock = clockOf()

fun offsetDateTime(): OffsetDateTime = OffsetDateTime.now(testClock)

fun localDateTime(): LocalDateTime = LocalDateTime.now(testClock)

fun localDate(): LocalDate = LocalDate.now(testClock)