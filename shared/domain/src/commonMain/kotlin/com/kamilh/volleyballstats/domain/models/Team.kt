package com.kamilh.volleyballstats.domain.models

import com.kamilh.volleyballstats.datetime.LocalDateTime

data class Team(
    val id: TeamId,
    val name: String,
    val teamImageUrl: Url,
    val logoUrl: Url,
    val updatedAt: LocalDateTime,
)