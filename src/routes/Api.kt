package com.kamilh.routes

import com.kamilh.models.MatchId
import com.kamilh.models.TourId
import com.kamilh.models.api.match.MatchResponse
import com.kamilh.models.api.match_report.MatchReportResponse
import com.kamilh.models.api.player_with_details.PlayerWithDetailsResponse
import com.kamilh.models.api.team.TeamResponse
import com.kamilh.models.api.tour.TourResponse
import com.kamilh.models.httprequest.Endpoint

class Api(private val baseUrl: String) {

    private inline fun <reified T> create(path: String, queryParams: Map<String, Any?> = emptyMap()): Endpoint<T> =
        Endpoint.create(baseUrl = baseUrl, path = path, queryParams = queryParams)

    fun getTours(): Endpoint<List<TourResponse>> =
        create(path = "tours")

    fun getTeams(tourId: TourId): Endpoint<List<TeamResponse>> =
        create(path = "teams", queryParams = tourId.queryParams)

    fun getPlayers(tourId: TourId): Endpoint<List<PlayerWithDetailsResponse>> =
        create(path = "players", queryParams = tourId.queryParams)

    fun getMatches(tourId: TourId): Endpoint<List<MatchResponse>> =
        create(path = "matches", queryParams = tourId.queryParams)

    fun getMatchReport(matchId: MatchId): Endpoint<MatchReportResponse> =
        create(path = "matches/report/${matchId.value}")

    private val TourId.queryParams: Map<String, Any?>
        get() = mapOf(TOUR_ID_QUERY_PARAM to value)

    companion object {
        private const val TOUR_ID_QUERY_PARAM = "tourId"
    }
}