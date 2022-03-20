@file:UseSerializers(
    PlayerIdSerializer::class,
    UrlSerializer::class,
    TeamIdSerializer::class,
    LocalDateTimeSerializer::class
)

package com.kamilh.models.api.team_player

import com.kamilh.datetime.LocalDateTime
import com.kamilh.models.PlayerId
import com.kamilh.models.TeamId
import com.kamilh.models.TeamPlayer
import com.kamilh.models.Url
import com.kamilh.models.api.adapters.LocalDateTimeSerializer
import com.kamilh.models.api.adapters.PlayerIdSerializer
import com.kamilh.models.api.adapters.TeamIdSerializer
import com.kamilh.models.api.adapters.UrlSerializer
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