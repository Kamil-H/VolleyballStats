package com.kamilh.models

import java.time.LocalDate

data class PlayerDetails(
    val date: LocalDate,
    val height: Int?,
    val weight: Int?,
    val range: Int?,
    val number: Int,
)