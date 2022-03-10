package com.kamilh.models

import java.time.LocalDate
import java.time.LocalDateTime

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