@file:UseSerializers(
    TourIdSerializer::class,
    SeasonSerializer::class,
    LocalDateSerializer::class,
    TeamIdSerializer::class,
    LocalDateTimeSerializer::class
)

package com.kamilh.volleyballstats.models.api.tour

import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.models.Season
import com.kamilh.volleyballstats.models.TourId
import com.kamilh.volleyballstats.models.api.adapters.*
import com.kamilh.volleyballstats.models.api.league.LeagueResponse
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