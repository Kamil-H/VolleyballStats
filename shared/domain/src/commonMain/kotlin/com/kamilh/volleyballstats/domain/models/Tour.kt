package com.kamilh.volleyballstats.domain.models

import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime

data class Tour(
    val id: TourId,
    val name: String,
    val season: Season,
    val league: League,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val updatedAt: LocalDateTime,
) {
    val isFinished = endDate != null
}
