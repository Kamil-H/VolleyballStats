package com.kamilh.repository.polishleague

import com.kamilh.models.MatchId
import com.kamilh.models.PlayerId
import com.kamilh.models.Season
import com.kamilh.models.Url
import com.kamilh.models.httprequest.HttpRequest
import com.kamilh.models.httprequest.UrlRequest

class PolishLeagueApi {

    fun getPlayers(tour: Season): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/players/tour/${tour.value}.html?memo={\"players\":{\"mainFilter\":\"letter\",\"subFilter\":\"all\"}}")
        )

    fun getTeams(tour: Season): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/teams/tour/${tour.value}.html")
        )

    fun getMatch(matchId: MatchId): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/games/id/${matchId.value}.html")
        )

    fun getAllMatches(tour: Season): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/games/tour/${tour.value}.html?memo={\"games\":{\"faza\":1,\"runda\":1}}")
        )

    fun getPlayerDetails(tour: Season, playerId: PlayerId): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/players/id/${playerId.value}/tour/${tour.value}.html")
        )

    fun getPlayerWithDetails(tour: Season, playerId: PlayerId): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/players/id/${playerId.value}/tour/${tour.value}.html")
        )

    fun getAllPlayers(): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/statsPlayers/tournament_1/all.html?memo={\"players\":{\"mainFilter\":\"letter\",\"subFilter\":\"all\"}}")
        )
}