package com.kamilh.volleyballstats.repository.extensions

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.datetime.parsePolishLeagueDate

internal fun String.toPolishLeagueLocalDate(): LocalDateTime? =
    if (this.isEmpty()) null else LocalDateTime.parsePolishLeagueDate(this)