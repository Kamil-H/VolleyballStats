package com.kamilh.models

data class Team(
    val id: Long,
    val name: String,
    val teamImageUrl: Url,
    val logoUrl: Url,
)