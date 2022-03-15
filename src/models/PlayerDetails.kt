package com.kamilh.models

import com.kamilh.datetime.LocalDate
import com.kamilh.datetime.LocalDateTime

data class PlayerDetails(
    val date: LocalDate,
    val height: Int?,
    val weight: Int?,
    val range: Int?,
    val number: Int,
    val updatedAt: LocalDateTime,
)