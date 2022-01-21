package com.kamilh.models

data class MatchPlayer(
    val id: PlayerId,
    val firstName: String,
    val isForeign: Boolean?,
    val lastName: String,
)
