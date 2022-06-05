package com.kamilh.volleyballstats.api.league

import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.domain.models.League
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