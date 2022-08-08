package com.kamilh.volleyballstats.clients.data

import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.api.StatsApi
import com.kamilh.volleyballstats.api.match.MatchResponse
import com.kamilh.volleyballstats.api.matchreport.MatchReportResponse
import com.kamilh.volleyballstats.api.player.PlayerResponse
import com.kamilh.volleyballstats.api.team.TeamResponse
import com.kamilh.volleyballstats.api.tour.TourResponse
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.network.HttpClient
import com.kamilh.volleyballstats.network.NetworkResult
import com.kamilh.volleyballstats.network.repository.PolishLeagueRepository
import me.tatarka.inject.annotations.Inject

interface StatsRepository : PolishLeagueRepository {

    suspend fun getPlayers(tour: Tour): NetworkResult<List<Player>>

    suspend fun getMatches(tour: Tour): NetworkResult<List<Match>>

    suspend fun getMatchReport(matchId: MatchId): NetworkResult<MatchReport>
}

@Inject
class HttpStatsRepository(
    private val httpClient: HttpClient,
    private val statsApi: StatsApi,
    private val matchMapper: ResponseMapper<Match, MatchResponse>,
    private val matchReportMapper: ResponseMapper<MatchReport, MatchReportResponse>,
    private val playerMapper: ResponseMapper<Player, PlayerResponse>,
    private val teamMapper: ResponseMapper<Team, TeamResponse>,
    private val tourMapper: ResponseMapper<Tour, TourResponse>,
) : StatsRepository {

    override suspend fun getTours(): NetworkResult<List<Tour>> =
        httpClient.execute(statsApi.getTours()).map { tours -> tours.map(tourMapper::from) }

    override suspend fun getTeams(tour: Tour): NetworkResult<List<Team>> =
        httpClient.execute(statsApi.getTeams(tour.id)).map { teams -> teams.map(teamMapper::from) }

    override suspend fun getPlayers(tour: Tour): NetworkResult<List<Player>> =
        httpClient.execute(statsApi.getPlayers(tour.id)).map { players -> players.map(playerMapper::from) }

    override suspend fun getMatches(tour: Tour): NetworkResult<List<Match>> =
        httpClient.execute(statsApi.getMatches(tour.id)).map { matches -> matches.map(matchMapper::from) }

    override suspend fun getMatchReport(matchId: MatchId): NetworkResult<MatchReport> =
        httpClient.execute(statsApi.getMatchReport(matchId)).map(matchReportMapper::from)
}
