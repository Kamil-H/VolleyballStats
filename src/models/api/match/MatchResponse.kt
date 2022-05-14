@file:UseSerializers(
    PlayerIdSerializer::class,
    TeamIdSerializer::class,
    ZonedDateTimeSerializer::class,
    MatchIdSerializer::class,
)

package com.kamilh.models.api.match

import com.kamilh.datetime.ZonedDateTime
import com.kamilh.models.MatchId
import com.kamilh.models.TeamId
import com.kamilh.models.api.adapters.MatchIdSerializer
import com.kamilh.models.api.adapters.PlayerIdSerializer
import com.kamilh.models.api.adapters.TeamIdSerializer
import com.kamilh.models.api.adapters.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class MatchResponse(
    val id: MatchId,
    val date: ZonedDateTime?,
    val home: TeamId,
    val away: TeamId,
    val hasReport: Boolean,
)