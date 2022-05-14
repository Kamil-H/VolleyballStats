package com.kamilh.volleyballstats.models.api.team_player

import com.kamilh.volleyballstats.models.TeamPlayer
import com.kamilh.volleyballstats.models.api.ResponseMapper
import me.tatarka.inject.annotations.Inject

@Inject
class TeamPlayerMapper : ResponseMapper<TeamPlayer, TeamPlayerResponse> {

    override fun to(from: TeamPlayer): TeamPlayerResponse =
        TeamPlayerResponse(
            id = from.id,
            name = from.name,
            imageUrl = from.imageUrl,
            team = from.team,
            specialization = from.specialization,
            updatedAt = from.updatedAt,
        )

    override fun from(from: TeamPlayerResponse): TeamPlayer =
        TeamPlayer(
            id = from.id,
            name = from.name,
            imageUrl = from.imageUrl,
            team = from.team,
            specialization = from.specialization,
            updatedAt = from.updatedAt,
        )
}