package com.kamilh.routes

import com.kamilh.models.*
import com.kamilh.models.api.player_with_details.PlayerWithDetailsResponse
import com.kamilh.models.httprequest.Endpoint
import com.kamilh.models.httprequest.HttpRequest

class Api(private val baseUrl: String) {

    private inline fun <reified T> create(path: String, queryParams: Map<String, Any?> = emptyMap()): Endpoint<T> =
        Endpoint.create(baseUrl = baseUrl, path = path, queryParams = queryParams)

    fun getTours(): HttpRequest<List<Tour>> =
        create(path = "tours")

    fun getTeams(tourId: TourId): HttpRequest<List<Team>> =
        create(path = "teams", queryParams = tourId.queryParams)

    fun getPlayers(tourId: TourId): HttpRequest<List<PlayerWithDetailsResponse>> =
        create(path = "players", queryParams = tourId.queryParams)

    fun getMatches(tourId: TourId): HttpRequest<List<Match>> =
        create(path = "matches", queryParams = tourId.queryParams)

    fun getMatchReport(matchId: MatchId): HttpRequest<MatchStatistics> =
        create(path = "matches/report/${matchId.value}")

    private val TourId.queryParams: Map<String, Any?>
        get() = mapOf(TOUR_ID_QUERY_PARAM to value)

    companion object {
        private const val TOUR_ID_QUERY_PARAM = "tourId"
    }
}