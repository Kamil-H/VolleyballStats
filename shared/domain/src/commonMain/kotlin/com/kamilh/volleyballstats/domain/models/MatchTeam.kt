package com.kamilh.volleyballstats.domain.models

data class MatchTeam(
    val teamId: TeamId,
    val code: String,
    val players: List<PlayerId>,
)
