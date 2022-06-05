@file:UseSerializers(
    PlayerIdSerializer::class,
    UrlSerializer::class,
    TeamIdSerializer::class,
    LocalDateTimeSerializer::class
)

package com.kamilh.volleyballstats.api.team_player

import com.kamilh.volleyballstats.api.adapters.LocalDateTimeSerializer
import com.kamilh.volleyballstats.api.adapters.PlayerIdSerializer
import com.kamilh.volleyballstats.api.adapters.TeamIdSerializer
import com.kamilh.volleyballstats.api.adapters.UrlSerializer
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.TeamPlayer
import com.kamilh.volleyballstats.domain.models.Url
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class TeamPlayerResponse(
    val id: PlayerId,
    val name: String,
    val imageUrl: Url?,
    val team: TeamId,
    val specialization: TeamPlayer.Specialization,
    val updatedAt: LocalDateTime,
)