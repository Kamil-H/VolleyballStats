package com.kamilh.volleyballstats.repository.extensions

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.datetime.parsePolishLeagueDate

private const val POLISH_LEAGUE_LOCAL_DATE_TIME_FORMAT = "dd.MM.yyyy, HH:mm"

internal fun String.toPolishLeagueLocalDate(): LocalDateTime? =
    if (this.isEmpty()) null else LocalDateTime.parsePolishLeagueDate(this)