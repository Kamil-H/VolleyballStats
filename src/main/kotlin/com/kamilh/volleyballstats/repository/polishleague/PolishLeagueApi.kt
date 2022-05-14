package com.kamilh.volleyballstats.repository.polishleague

import com.kamilh.volleyballstats.models.MatchId
import com.kamilh.volleyballstats.models.PlayerId
import com.kamilh.volleyballstats.models.Season
import com.kamilh.volleyballstats.models.Url
import com.kamilh.volleyballstats.models.httprequest.HttpRequest
import com.kamilh.volleyballstats.models.httprequest.UrlRequest
import me.tatarka.inject.annotations.Inject

@Inject
class PolishLeagueApi {

    fun getPlayers(season: Season): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/players/tour/${season.value}.html?memo={\"players\":{\"mainFilter\":\"letter\",\"subFilter\":\"all\"}}")
        )

    fun getTeams(season: Season): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/teams/tour/${season.value}.html")
        )

    fun getMatch(matchId: MatchId): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/games/id/${matchId.value}.html")
        )

    fun getAllMatches(season: Season): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/games/tour/${season.value}.html?memo={\"games\":{\"faza\":all,\"runda\":all}}")
        )

    fun getPlayerDetails(season: Season, playerId: PlayerId): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/players/id/${playerId.value}/tour/${season.value}.html")
        )

    fun getPlayerWithDetails(season: Season, playerId: PlayerId): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/players/id/${playerId.value}/tour/${season.value}.html")
        )

    fun getAllPlayers(): HttpRequest<String> =
        UrlRequest.getHtml(
            Url.create("https://www.plusliga.pl/statsPlayers/tournament_1/all.html?memo={\"players\":{\"mainFilter\":\"letter\",\"subFilter\":\"all\"}}")
        )
}