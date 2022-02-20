package com.kamilh.models

import java.time.LocalDate
import java.time.LocalDateTime

data class Tour(
    val name: String,
    val year: TourYear,
    val league: League,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val winnerId: TeamId?,
    val updatedAt: LocalDateTime,
) {
    val isFinished = endDate != null
}