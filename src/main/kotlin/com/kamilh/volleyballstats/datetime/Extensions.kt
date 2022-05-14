package com.kamilh.volleyballstats.datetime

internal fun <T> String.parseDate(parser: (String) -> T): T? {
    if (isEmpty()) return null
    return try {
        parser(this)
    } catch (exception: Exception) {
        null
    }
}