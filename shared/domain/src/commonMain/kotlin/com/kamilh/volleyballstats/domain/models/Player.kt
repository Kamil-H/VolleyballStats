package com.kamilh.volleyballstats.domain.models

import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime

data class Player(
    val id: PlayerId,
    val name: String,
    val imageUrl: Url?,
    val team: TeamId,
    val specialization: Specialization,
    val date: LocalDate,
    val height: Int?,
    val weight: Int?,
    val range: Int?,
    val number: Int,
    val updatedAt: LocalDateTime,
)
