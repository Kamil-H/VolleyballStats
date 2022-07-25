@file:UseSerializers(
    LocalDateSerializer::class,
    LocalDateTimeSerializer::class,
    PlayerIdSerializer::class,
    UrlSerializer::class,
    TeamIdSerializer::class,
)

package com.kamilh.volleyballstats.api

import com.kamilh.volleyballstats.api.adapters.*
import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.Url
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class PlayerResponse(
    val id: PlayerId,
    val name: String,
    val imageUrl: Url?,
    val team: TeamId,
    val specialization: Specialization,
    val date: LocalDate,
    val height: Int?,
    val weight: Int?,
    val range: Int?,
    val number: Int,
    val updatedAt: LocalDateTime,
)
