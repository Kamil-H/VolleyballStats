package com.kamilh.volleyballstats.api

import com.kamilh.volleyballstats.api.match.MatchResponse
import com.kamilh.volleyballstats.api.matchreport.MatchReportResponse
import com.kamilh.volleyballstats.api.player.PlayerResponse
import com.kamilh.volleyballstats.api.team.TeamResponse
import com.kamilh.volleyballstats.api.tour.TourResponse
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.network.httprequest.Endpoint

class Api(private val baseUrl: String) {

    private inline fun <reified T> create(path: String, queryParams: Map<String, Any?> = emptyMap()): Endpoint<T> =
        Endpoint.create(baseUrl = baseUrl, path = path, queryParams = queryParams)

    fun getTours(): Endpoint<List<TourResponse>> =
        create(path = ApiConstants.PATH_SEGMENT_TOURS)

    fun getTeams(tourId: TourId): Endpoint<List<TeamResponse>> =
        create(path = ApiConstants.PATH_SEGMENT_TEAMS, queryParams = tourId.queryParams)

    fun getPlayers(tourId: TourId): Endpoint<List<PlayerResponse>> =
        create(path = ApiConstants.PATH_SEGMENT_PLAYERS, queryParams = tourId.queryParams)

    fun getMatches(tourId: TourId): Endpoint<List<MatchResponse>> =
        create(path = ApiConstants.PATH_SEGMENT_MATCHES, queryParams = tourId.queryParams)

    fun getMatchReport(matchId: MatchId): Endpoint<MatchReportResponse> =
        create(path = "${ApiConstants.PATH_SEGMENT_MATCHES}/${ApiConstants.PATH_SEGMENT_REPORT}/${matchId.value}")

    private val TourId.queryParams: Map<String, Any?>
        get() = mapOf(ApiConstants.QUERY_PARAM_TOUR_ID to value)
}
