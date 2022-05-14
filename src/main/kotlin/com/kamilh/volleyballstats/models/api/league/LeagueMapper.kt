package com.kamilh.volleyballstats.models.api.league

import com.kamilh.volleyballstats.models.League
import com.kamilh.volleyballstats.models.api.ResponseMapper
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