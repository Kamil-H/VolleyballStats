package com.kamilh.volleyballstats.domain

import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.models.Match
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.utils.zonedDateTime

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