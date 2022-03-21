@file:UseSerializers(
    TourIdSerializer::class,
    SeasonSerializer::class,
    LocalDateSerializer::class,
    TeamIdSerializer::class,
    LocalDateTimeSerializer::class
)

package com.kamilh.models.api.tour

import com.kamilh.datetime.LocalDate
import com.kamilh.datetime.LocalDateTime
import com.kamilh.models.Season
import com.kamilh.models.TourId
import com.kamilh.models.api.adapters.*
import com.kamilh.models.api.league.LeagueResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class TourResponse(
    val id: TourId,
    val name: String,
    val season: Season,
    val league: LeagueResponse,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val updatedAt: LocalDateTime,
)