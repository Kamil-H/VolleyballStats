package com.kamilh.volleyballstats.models

data class MatchTeam(
    val teamId: TeamId,
    val code: String,
    val players: List<PlayerId>,
)