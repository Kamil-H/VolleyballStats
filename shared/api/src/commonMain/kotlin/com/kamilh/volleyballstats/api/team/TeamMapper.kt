package com.kamilh.volleyballstats.api.team

import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.domain.models.Team
import me.tatarka.inject.annotations.Inject

@Inject
class TeamMapper : ResponseMapper<Team, TeamResponse> {

    override fun to(from: Team): TeamResponse =
        TeamResponse(
            id = from.id,
            name = from.name,
            teamImageUrl = from.teamImageUrl,
            logoUrl = from.logoUrl,
            updatedAt = from.updatedAt,
        )

    override fun from(from: TeamResponse): Team =
        Team(
            id = from.id,
            name = from.name,
            teamImageUrl = from.teamImageUrl,
            logoUrl = from.logoUrl,
            updatedAt = from.updatedAt,
        )
}
