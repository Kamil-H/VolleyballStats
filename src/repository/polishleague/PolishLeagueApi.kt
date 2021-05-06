package com.kamilh.repository.polishleague

import com.kamilh.models.MatchId
import com.kamilh.models.Tour
import com.kamilh.models.httprequest.HttpRequest
import com.kamilh.models.Url
import com.kamilh.models.httprequest.UrlRequest

class PolishLeagueApi {

    fun getPlayers(tour: Tour): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/players/tour/${tour.value}.html?memo={\"players\":{\"mainFilter\":\"letter\",\"subFilter\":\"all\"}}")
        )

    fun getTeams(tour: Tour): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/teams/tour/${tour.value}.html")
        )

    fun getMatch(matchId: MatchId): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/games/id/${matchId.value}.html")
        )

    fun getAllMatches(tour: Tour): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/games/tour/${tour.value}.html")
        )
}