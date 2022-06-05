@file:UseSerializers(
    PlayerIdSerializer::class,
    TeamIdSerializer::class,
    ZonedDateTimeSerializer::class,
    MatchIdSerializer::class,
)

package com.kamilh.volleyballstats.api.match

import com.kamilh.volleyballstats.api.adapters.MatchIdSerializer
import com.kamilh.volleyballstats.api.adapters.PlayerIdSerializer
import com.kamilh.volleyballstats.api.adapters.TeamIdSerializer
import com.kamilh.volleyballstats.api.adapters.ZonedDateTimeSerializer
import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.TeamId
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