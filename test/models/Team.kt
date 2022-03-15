package com.kamilh.models

import com.kamilh.datetime.LocalDateTime
import com.kamilh.utils.localDateTime

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