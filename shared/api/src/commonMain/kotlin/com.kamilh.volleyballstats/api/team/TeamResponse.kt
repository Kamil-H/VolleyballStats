@file:UseSerializers(
    UrlSerializer::class,
    TeamIdSerializer::class,
    LocalDateTimeSerializer::class
)

package com.kamilh.volleyballstats.api.team

import com.kamilh.volleyballstats.api.adapters.LocalDateTimeSerializer
import com.kamilh.volleyballstats.api.adapters.TeamIdSerializer
import com.kamilh.volleyballstats.api.adapters.UrlSerializer
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.Url
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