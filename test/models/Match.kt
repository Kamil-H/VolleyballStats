package com.kamilh.models

import com.kamilh.datetime.ZonedDateTime
import com.kamilh.utils.zonedDateTime

fun matchOf(
    id: MatchId = matchIdOf(),
    date: ZonedDateTime? = zonedDateTime(),
    home: TeamId = teamIdOf(),
    away: TeamId = teamIdOf(),
    hasReport: Boolean = false,
): Match = Match(
    id = id,
    date = date,
    home = home,
    away = away,
    hasReport = hasReport,
)