package com.kamilh.extensions

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val offsetDateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

fun OffsetDateTime.toIsoString(): String = format(offsetDateTimeFormatter)

fun String.toOffsetDateTime(): OffsetDateTime? {
    if (isEmpty()) return null
    return try {
        OffsetDateTime.parse(this, offsetDateTimeFormatter)
    } catch (exception: DateTimeParseException) {
        null
    }
}