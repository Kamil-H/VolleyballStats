package com.kamilh.volleyballstats.api.match

import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.domain.models.Match
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