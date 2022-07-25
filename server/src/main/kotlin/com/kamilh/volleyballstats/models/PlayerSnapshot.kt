package com.kamilh.volleyballstats.models

import com.kamilh.volleyballstats.domain.models.PlayerId

data class PlayerSnapshot(
    val id: PlayerId,
    val name: String,
)
