package com.kamilh.volleyballstats.models.api.player_details

import com.kamilh.volleyballstats.models.PlayerDetails
import com.kamilh.volleyballstats.models.api.ResponseMapper
import me.tatarka.inject.annotations.Inject

@Inject
class PlayerDetailsMapper : ResponseMapper<PlayerDetails, PlayerDetailsResponse> {

    override fun to(from: PlayerDetails): PlayerDetailsResponse =
        PlayerDetailsResponse(
            date = from.date,
            height = from.height,
            weight = from.weight,
            range = from.range,
            number = from.number,
            updatedAt = from.updatedAt,
        )

    override fun from(from: PlayerDetailsResponse): PlayerDetails =
        PlayerDetails(
            date = from.date,
            height = from.height,
            weight = from.weight,
            range = from.range,
            number = from.number,
            updatedAt = from.updatedAt,
        )
}