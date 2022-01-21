package com.kamilh.extensions

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val ISO_OFFSET_DATE_TIME = DateTimeFormatter.ISO_OFFSET_DATE_TIME
private val ISO_LOCAL_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME
private val ISO_LOCAL_DATE = DateTimeFormatter.ISO_LOCAL_DATE
private const val GMT_PLUS_1 = "GMT+1"

fun OffsetDateTime.toIsoString(): String = format(ISO_OFFSET_DATE_TIME)
fun String.toOffsetDateTime(): OffsetDateTime? = parseDate {
    OffsetDateTime.parse(this, ISO_OFFSET_DATE_TIME)
}

fun LocalDateTime.toIsoString(): String = format(ISO_LOCAL_DATE_TIME)
fun String.toLocalDateTime(): LocalDateTime? = parseDate {
    LocalDateTime.parse(this, ISO_LOCAL_DATE_TIME)
}

fun LocalDateTime.atPolandOffset(): OffsetDateTime = atZone(ZoneId.of(GMT_PLUS_1)).toOffsetDateTime()

fun LocalDate.toIsoString(): String = format(ISO_LOCAL_DATE)
fun String.toLocalDate(): LocalDate? = parseDate {
    LocalDate.parse(this, ISO_LOCAL_DATE)
}

private fun <T> String.parseDate(parser: () -> T): T? {
    if (isEmpty()) return null
    return try {
        parser()
    } catch (exception: DateTimeParseException) {
        null
    }
}