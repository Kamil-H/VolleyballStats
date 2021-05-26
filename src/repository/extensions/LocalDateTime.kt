package com.kamilh.repository.extensions

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val POLISH_LEAGUE_LOCAL_DATE_TIME_FORMAT = "dd.MM.yyyy, HH:mm"
private val polishLeagueLocalDateTimeFormatter = DateTimeFormatter.ofPattern(POLISH_LEAGUE_LOCAL_DATE_TIME_FORMAT)

internal fun String.toPolishLeagueLocalDate(): LocalDateTime? = LocalDateTime.parse(this, polishLeagueLocalDateTimeFormatter)
internal fun LocalDateTime.toPolishLeagueDateString(): String = format(polishLeagueLocalDateTimeFormatter)