package com.kamilh.volleyballstats.api

import com.kamilh.volleyballstats.domain.models.Player
import me.tatarka.inject.annotations.Inject

@Inject
class PlayerMapper : ResponseMapper<Player, PlayerResponse> {

    override fun to(from: Player): PlayerResponse =
        PlayerResponse(
            id = from.id,
            name = from.name,
            imageUrl = from.imageUrl,
            team = from.team,
            specialization = from.specialization,
            date = from.date,
            height = from.height,
            weight = from.weight,
            range = from.range,
            number = from.number,
            updatedAt = from.updatedAt,
        )

    override fun from(from: PlayerResponse): Player =
        Player(
            id = from.id,
            name = from.name,
            imageUrl = from.imageUrl,
            team = from.team,
            specialization = from.specialization,
            date = from.date,
            height = from.height,
            weight = from.weight,
            range = from.range,
            number = from.number,
            updatedAt = from.updatedAt,
        )
}
