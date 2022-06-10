package com.kamilh.volleyballstats.models

import com.kamilh.volleyballstats.domain.models.Player

data class PlayerWithDetails(
    val teamPlayer: TeamPlayer,
    val details: PlayerDetails,
)

fun PlayerWithDetails.toPlayer(): Player =
    Player(
        id = teamPlayer.id,
        name = teamPlayer.name,
        imageUrl = teamPlayer.imageUrl,
        team = teamPlayer.team,
        specialization = teamPlayer.specialization,
        date = details.date,
        height = details.height,
        weight = details.weight,
        range = details.range,
        number = details.number,
        updatedAt = details.updatedAt,
    )