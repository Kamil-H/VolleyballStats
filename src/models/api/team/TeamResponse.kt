@file:UseSerializers(
    UrlSerializer::class,
    TeamIdSerializer::class,
    LocalDateTimeSerializer::class
)

package com.kamilh.models.api.team

import com.kamilh.datetime.LocalDateTime
import com.kamilh.models.TeamId
import com.kamilh.models.Url
import com.kamilh.models.api.adapters.LocalDateTimeSerializer
import com.kamilh.models.api.adapters.TeamIdSerializer
import com.kamilh.models.api.adapters.UrlSerializer
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