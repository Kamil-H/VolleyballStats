@file:UseSerializers(LocalDateSerializer::class, LocalDateTimeSerializer::class)

package com.kamilh.models.api.player_details

import com.kamilh.datetime.LocalDate
import com.kamilh.datetime.LocalDateTime
import com.kamilh.models.api.adapters.LocalDateSerializer
import com.kamilh.models.api.adapters.LocalDateTimeSerializer
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