package com.kamilh.volleyballstats.models

import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime

data class PlayerDetails(
    val date: LocalDate,
    val height: Int?,
    val weight: Int?,
    val range: Int?,
    val number: Int,
    val updatedAt: LocalDateTime,
)