@file:UseSerializers(
    PlayerIdSerializer::class,
    UrlSerializer::class,
    TeamIdSerializer::class,
    LocalDateTimeSerializer::class
)

package com.kamilh.volleyballstats.models.api.team_player

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.models.PlayerId
import com.kamilh.volleyballstats.models.TeamId
import com.kamilh.volleyballstats.models.TeamPlayer
import com.kamilh.volleyballstats.models.Url
import com.kamilh.volleyballstats.models.api.adapters.LocalDateTimeSerializer
import com.kamilh.volleyballstats.models.api.adapters.PlayerIdSerializer
import com.kamilh.volleyballstats.models.api.adapters.TeamIdSerializer
import com.kamilh.volleyballstats.models.api.adapters.UrlSerializer
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