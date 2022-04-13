package com.kamilh.models.api.match

import com.kamilh.datetime.ZonedDateTime
import com.kamilh.models.MatchId
import com.kamilh.models.TeamId

class MatchResponse(
    val id: MatchId,
    val date: ZonedDateTime?,
    val home: TeamId,
    val away: TeamId,
    val hasReport: Boolean,
)