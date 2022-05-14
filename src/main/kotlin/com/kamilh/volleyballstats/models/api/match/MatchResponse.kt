@file:UseSerializers(
    PlayerIdSerializer::class,
    TeamIdSerializer::class,
    ZonedDateTimeSerializer::class,
    MatchIdSerializer::class,
)

package com.kamilh.volleyballstats.models.api.match

import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.models.MatchId
import com.kamilh.volleyballstats.models.TeamId
import com.kamilh.volleyballstats.models.api.adapters.MatchIdSerializer
import com.kamilh.volleyballstats.models.api.adapters.PlayerIdSerializer
import com.kamilh.volleyballstats.models.api.adapters.TeamIdSerializer
import com.kamilh.volleyballstats.models.api.adapters.ZonedDateTimeSerializer
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