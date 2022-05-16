package com.kamilh.volleyballstats.models.api.match

import com.kamilh.volleyballstats.models.Match
import com.kamilh.volleyballstats.models.api.ResponseMapper
import me.tatarka.inject.annotations.Inject

@Inject
class MatchMapper : ResponseMapper<Match, MatchResponse> {

    override fun to(from: Match): MatchResponse =
        MatchResponse(
            id = from.id,
            date = from.date,
            home = from.home,
            away = from.away,
            hasReport = from.hasReport,
        )

    override fun from(from: MatchResponse): Match =
        Match(
            id = from.id,
            date = from.date,
            home = from.home,
            away = from.away,
            hasReport = from.hasReport,
        )
}