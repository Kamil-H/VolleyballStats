package com.kamilh.models

import com.kamilh.datetime.ZonedDateTime

data class Match(
    val id: MatchId,
    val date: ZonedDateTime?,
    val home: TeamId,
    val away: TeamId,
    val hasReport: Boolean,
)