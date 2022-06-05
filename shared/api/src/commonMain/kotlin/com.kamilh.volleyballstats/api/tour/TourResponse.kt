@file:UseSerializers(
    TourIdSerializer::class,
    SeasonSerializer::class,
    LocalDateSerializer::class,
    TeamIdSerializer::class,
    LocalDateTimeSerializer::class
)

package com.kamilh.volleyballstats.api.tour

import com.kamilh.volleyballstats.api.adapters.*
import com.kamilh.volleyballstats.api.league.LeagueResponse
import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.TourId
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