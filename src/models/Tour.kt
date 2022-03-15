package com.kamilh.models

import com.kamilh.datetime.LocalDate
import com.kamilh.datetime.LocalDateTime

data class Tour(
    val id: TourId,
    val name: String,
    val season: Season,
    val league: League,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val winnerId: TeamId?,
    val updatedAt: LocalDateTime,
) {
    val isFinished = endDate != null
}