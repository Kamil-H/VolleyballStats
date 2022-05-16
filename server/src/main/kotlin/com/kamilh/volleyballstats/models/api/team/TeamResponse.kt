@file:UseSerializers(
    UrlSerializer::class,
    TeamIdSerializer::class,
    LocalDateTimeSerializer::class
)

package com.kamilh.volleyballstats.models.api.team

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.models.TeamId
import com.kamilh.volleyballstats.models.Url
import com.kamilh.volleyballstats.models.api.adapters.LocalDateTimeSerializer
import com.kamilh.volleyballstats.models.api.adapters.TeamIdSerializer
import com.kamilh.volleyballstats.models.api.adapters.UrlSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class TeamResponse(
    val id: TeamId,
    val name: String,
    val teamImageUrl: Url,
    val logoUrl: Url,
    val updatedAt: LocalDateTime,
)