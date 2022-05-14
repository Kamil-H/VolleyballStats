package com.kamilh.volleyballstats.models

import com.kamilh.volleyballstats.datetime.ZonedDateTime

data class Match(
    val id: MatchId,
    val date: ZonedDateTime?,
    val home: TeamId,
    val away: TeamId,
    val hasReport: Boolean,
)