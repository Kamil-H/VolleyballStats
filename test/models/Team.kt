package com.kamilh.models

fun teamOf(
    id: TeamId = teamIdOf(),
    name: String = "",
    teamImageUrl: Url = urlOf(),
    logoUrl: Url = urlOf(),
): Team = Team(
    id = id,
    name = name,
    teamImageUrl = teamImageUrl,
    logoUrl = logoUrl,
)