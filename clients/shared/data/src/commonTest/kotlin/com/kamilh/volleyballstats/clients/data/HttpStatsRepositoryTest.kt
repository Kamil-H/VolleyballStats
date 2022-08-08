package com.kamilh.volleyballstats.clients.data

import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.api.StatsApi
import com.kamilh.volleyballstats.api.league.LeagueMapper
import com.kamilh.volleyballstats.api.match.MatchMapper
import com.kamilh.volleyballstats.api.match.MatchResponse
import com.kamilh.volleyballstats.api.matchreport.MatchReportMapper
import com.kamilh.volleyballstats.api.matchreport.MatchReportResponse
import com.kamilh.volleyballstats.api.matchreport.MatchSetResponse
import com.kamilh.volleyballstats.api.matchreport.MatchTeamResponse
import com.kamilh.volleyballstats.api.player.PlayerMapper
import com.kamilh.volleyballstats.api.player.PlayerResponse
import com.kamilh.volleyballstats.api.team.TeamMapper
import com.kamilh.volleyballstats.api.team.TeamResponse
import com.kamilh.volleyballstats.api.tour.TourMapper
import com.kamilh.volleyballstats.api.tour.TourResponse
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.network.HttpClient
import com.kamilh.volleyballstats.network.client.httpClientOf
import com.kamilh.volleyballstats.network.result.networkFailureOf
import com.kamilh.volleyballstats.network.result.networkSuccessOf
import com.kamilh.volleyballstats.repository.polishleague.networkErrorOf
import com.kamilh.volleyballstats.utils.localDateTime
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class HttpStatsRepositoryTest {

    private fun <T> httpStatsRepositoryTestOf(
        httpClient: HttpClient = httpClientOf<T>(networkFailureOf(networkErrorOf())),
        statsApi: StatsApi = StatsApi(baseUrl = ""),
        matchMapper: ResponseMapper<Match, MatchResponse> = MatchMapper(),
        matchReportMapper: ResponseMapper<MatchReport, MatchReportResponse> = MatchReportMapper(),
        playerMapper: ResponseMapper<Player, PlayerResponse> = PlayerMapper(),
        teamMapper: ResponseMapper<Team, TeamResponse> = TeamMapper(),
        tourMapper: ResponseMapper<Tour, TourResponse> = TourMapper(LeagueMapper()),
    ): HttpStatsRepository = HttpStatsRepository(
        httpClient = httpClient,
        statsApi = statsApi,
        matchMapper = matchMapper,
        matchReportMapper = matchReportMapper,
        playerMapper = playerMapper,
        teamMapper = teamMapper,
        tourMapper = tourMapper,
    )

    @Test
    fun `getTours returns success when statsApi's getTours returns value`() = runTest {
        // GIVEN
        val networkResult = networkSuccessOf<List<TourResponse>>(emptyList())

        // WHEN
        val result = httpStatsRepositoryTestOf<List<TourResponse>>(
            httpClient = httpClientOf(networkResult = networkResult)
        ).getTours()

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `getTours returns error when statsApi's getTours returns value`() = runTest {
        // GIVEN
        val networkResult = networkFailureOf<List<TourResponse>>(networkErrorOf())

        // WHEN
        val result = httpStatsRepositoryTestOf<List<TourResponse>>(
            httpClient = httpClientOf(networkResult = networkResult)
        ).getTours()

        // THEN
        result.assertFailure()
    }

    @Test
    fun `getTeams returns success when statsApi's getTeams returns value`() = runTest {
        // GIVEN
        val networkResult = networkSuccessOf<List<TeamResponse>>(emptyList())
        val tour = tourOf()

        // WHEN
        val result = httpStatsRepositoryTestOf<List<TeamResponse>>(
            httpClient = httpClientOf(networkResult = networkResult)
        ).getTeams(tour)

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `getTeams returns error when statsApi's getTeams returns value`() = runTest {
        // GIVEN
        val networkResult = networkFailureOf<List<TeamResponse>>(networkErrorOf())
        val tour = tourOf()

        // WHEN
        val result = httpStatsRepositoryTestOf<List<TeamResponse>>(
            httpClient = httpClientOf(networkResult = networkResult)
        ).getTeams(tour)

        // THEN
        result.assertFailure()
    }

    @Test
    fun `getPlayers returns success when statsApi's getPlayers returns value`() = runTest {
        // GIVEN
        val networkResult = networkSuccessOf<List<PlayerResponse>>(emptyList())
        val tour = tourOf()

        // WHEN
        val result = httpStatsRepositoryTestOf<List<PlayerResponse>>(
            httpClient = httpClientOf(networkResult = networkResult)
        ).getPlayers(tour)

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `getPlayers returns error when statsApi's getPlayers returns value`() = runTest {
        // GIVEN
        val networkResult = networkFailureOf<List<PlayerResponse>>(networkErrorOf())
        val tour = tourOf()

        // WHEN
        val result = httpStatsRepositoryTestOf<List<PlayerResponse>>(
            httpClient = httpClientOf(networkResult = networkResult)
        ).getPlayers(tour)

        // THEN
        result.assertFailure()
    }

    @Test
    fun `getMatches returns success when statsApi's getMatches returns value`() = runTest {
        // GIVEN
        val networkResult = networkSuccessOf<List<MatchResponse>>(emptyList())
        val tour = tourOf()

        // WHEN
        val result = httpStatsRepositoryTestOf<List<MatchResponse>>(
            httpClient = httpClientOf(networkResult = networkResult)
        ).getMatches(tour)

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `getMatches returns error when statsApi's getMatches returns value`() = runTest {
        // GIVEN
        val networkResult = networkFailureOf<List<MatchResponse>>(networkErrorOf())
        val tour = tourOf()

        // WHEN
        val result = httpStatsRepositoryTestOf<List<MatchResponse>>(
            httpClient = httpClientOf(networkResult = networkResult)
        ).getMatches(tour)

        // THEN
        result.assertFailure()
    }

    @Test
    fun `getMatchReport returns success when statsApi's getMatchReport returns value`() = runTest {
        // GIVEN
        val networkResult = networkSuccessOf<MatchReportResponse>(matchReportResponseOf())

        // WHEN
        val result = httpStatsRepositoryTestOf<MatchReportResponse>(
            httpClient = httpClientOf(networkResult = networkResult)
        ).getMatchReport(matchIdOf())

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `getMatchReport returns error when getMatchReport's getMatches returns value`() = runTest {
        // GIVEN
        val networkResult = networkFailureOf<MatchReportResponse>(networkErrorOf())

        // WHEN
        val result = httpStatsRepositoryTestOf<MatchReportResponse>(
            httpClient = httpClientOf(networkResult = networkResult)
        ).getMatchReport(matchIdOf())

        // THEN
        result.assertFailure()
    }
}

private fun matchReportResponseOf(
    matchId: MatchId = matchIdOf(),
    sets: List<MatchSetResponse> = emptyList(),
    home: MatchTeamResponse = matchTeamResponseOf(),
    away: MatchTeamResponse = matchTeamResponseOf(),
    mvp: PlayerId = playerIdOf(),
    bestPlayer: PlayerId? = null,
    updatedAt: LocalDateTime = localDateTime(),
    phase: Phase = Phase.PlayOff,
): MatchReportResponse = MatchReportResponse(
    matchId = matchId,
    sets = sets,
    home = home,
    away = away,
    mvp = mvp,
    bestPlayer = bestPlayer,
    updatedAt = updatedAt,
    phase = phase,
)

private fun matchTeamResponseOf(
    teamId: TeamId = teamIdOf(),
    code: String = "",
    players: List<PlayerId> = emptyList(),
): MatchTeamResponse = MatchTeamResponse(
    teamId = teamId,
    code = code,
    players = players,
)
