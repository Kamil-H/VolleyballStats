package com.kamilh.models.api.league

import com.kamilh.models.League
import com.kamilh.models.api.ResponseMapper
import me.tatarka.inject.annotations.Inject

@Inject
class LeagueMapper : ResponseMapper<League, LeagueResponse> {

    override fun to(from: League): LeagueResponse =
        LeagueResponse(
            country = from.country,
            division = from.division,
        )

    override fun from(from: LeagueResponse): League =
        League(
            country = from.country,
            division = from.division,
        )
}