package com.kamilh.models

import java.time.LocalDateTime

fun teamOf(
    id: TeamId = teamIdOf(),
    name: String = "",
    teamImageUrl: Url = urlOf(),
    logoUrl: Url = urlOf(),
    updatedAt: LocalDateTime = LocalDateTime.now(),
): Team = Team(
    id = id,
    name = name,
    teamImageUrl = teamImageUrl,
    logoUrl = logoUrl,
    updatedAt = updatedAt,
)