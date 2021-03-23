package com.kamilh.models

data class Player(
    val id: Long,
    val name: String,
    val imageUrl: Url?,
    val team: Long,
    val position: Int,
)