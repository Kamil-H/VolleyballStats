package com.kamilh.models.api.team

import com.kamilh.models.Team
import com.kamilh.models.api.ResponseMapper
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