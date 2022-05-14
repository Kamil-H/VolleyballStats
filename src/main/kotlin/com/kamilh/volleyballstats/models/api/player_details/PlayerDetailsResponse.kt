@file:UseSerializers(LocalDateSerializer::class, LocalDateTimeSerializer::class)

package com.kamilh.volleyballstats.models.api.player_details

import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.models.api.adapters.LocalDateSerializer
import com.kamilh.volleyballstats.models.api.adapters.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class PlayerDetailsResponse(
    val date: LocalDate,
    val height: Int?,
    val weight: Int?,
    val range: Int?,
    val number: Int,
    val updatedAt: LocalDateTime,
)