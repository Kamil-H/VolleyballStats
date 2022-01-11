package com.kamilh.models

import java.time.LocalDate
import java.time.LocalDateTime

data class PlayerDetails(
    val date: LocalDate,
    val height: Int?,
    val weight: Int?,
    val range: Int?,
    val number: Int,
    val updatedAt: LocalDateTime,
)