package com.kamilh.volleyballstats.models

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.utils.localDateTime

fun teamOf(
    id: TeamId = teamIdOf(),
    name: String = "",
    teamImageUrl: Url = urlOf(),
    logoUrl: Url = urlOf(),
    updatedAt: LocalDateTime = localDateTime(),
): Team = Team(
    id = id,
    name = name,
    teamImageUrl = teamImageUrl,
    logoUrl = logoUrl,
    updatedAt = updatedAt,
)