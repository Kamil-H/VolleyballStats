package com.kamilh.models

data class Player(
    val id: PlayerId,
    val name: String,
    val imageUrl: Url?,
    val team: TeamId,
    val position: Int,
)