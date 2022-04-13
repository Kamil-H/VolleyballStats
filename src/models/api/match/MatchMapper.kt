package com.kamilh.models.api.match

import com.kamilh.models.Match
import com.kamilh.models.api.ResponseMapper

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