@file:UseSerializers(
    LocalDateSerializer::class,
    LocalDateTimeSerializer::class
)

package com.kamilh.volleyballstats.api.player_details

import com.kamilh.volleyballstats.api.adapters.LocalDateSerializer
import com.kamilh.volleyballstats.api.adapters.LocalDateTimeSerializer
import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
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