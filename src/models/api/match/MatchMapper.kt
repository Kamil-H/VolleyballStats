package com.kamilh.models.api.match

import com.kamilh.models.Match
import com.kamilh.models.api.ResponseMapper

class MatchMapper : ResponseMapper<Match, MatchResponse> {

    override fun to(from: Match): MatchResponse =
        MatchResponse()

    override fun from(from: MatchResponse): Match {
        TODO("Not yet implemented")
    }
}