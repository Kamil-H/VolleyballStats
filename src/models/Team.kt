package com.kamilh.models

import java.time.LocalDateTime

data class Team(
    val id: TeamId,
    val name: String,
    val teamImageUrl: Url,
    val logoUrl: Url,
    val updatedAt: LocalDateTime,
)