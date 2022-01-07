package com.kamilh.repository.polishleague

import com.kamilh.models.*
import com.kamilh.repository.HttpClient
import com.kamilh.repository.models.mappers.HtmlMapper
import com.kamilh.repository.models.mappers.MatchResponseToMatchReportMapper
import com.kamilh.repository.parsing.ParseErrorHandler
import repository.parsing.ParseResult

interface PolishLeagueRepository {

    suspend fun getAllTeams(tour: TourYear): NetworkResult<List<Team>>

    suspend fun getAllPlayers(tour: TourYear): NetworkResult<List<Player>>

    suspend fun getAllMatches(tour: TourYear): NetworkResult<List<AllMatchesItem>>

    suspend fun getMatchReportId(matchId: MatchId): NetworkResult<MatchReportId>

    suspend fun getMatchReport(matchReportId: MatchReportId, tour: TourYear): NetworkResult<MatchReport>

    suspend fun getPlayerDetails(tour: TourYear, playerId: PlayerId): NetworkResult<PlayerDetails>
}

class HttpPolishLeagueRepository(
    private val httpClient: HttpClient,
    private val polishLeagueApi: PolishLeagueApi,
    private val htmlToTeamMapper: HtmlMapper<List<Team>>,
    private val htmlToPlayerMapper: HtmlMapper<List<Player>>,
    private val htmlToAllMatchesItemMapper: HtmlMapper<List<AllMatchesItem>>,
    private val htmlToMatchReportId: HtmlMapper<MatchReportId>,
    private val htmlToPlayerDetailsMapper: HtmlMapper<PlayerDetails>,
    private val matchReportEndpoint: MatchReportEndpoint,
    private val parseErrorHandler: ParseErrorHandler,
    private val matchResponseStorage: MatchResponseStorage,
    private val matchResponseToMatchReportMapper: MatchResponseToMatchReportMapper,
) : PolishLeagueRepository {

    override suspend fun getAllTeams(tour: TourYear): NetworkResult<List<Team>> =
        httpClient.execute(polishLeagueApi.getTeams(tour)).parseHtml(htmlToTeamMapper::map)

    override suspend fun getAllPlayers(tour: TourYear): NetworkResult<List<Player>> =
        httpClient.execute(polishLeagueApi.getPlayers(tour)).parseHtml(htmlToPlayerMapper::map)

    override suspend fun getAllMatches(tour: TourYear): NetworkResult<List<AllMatchesItem>> =
        httpClient.execute(polishLeagueApi.getAllMatches(tour)).parseHtml(htmlToAllMatchesItemMapper::map)

    override suspend fun getMatchReportId(matchId: MatchId): NetworkResult<MatchReportId> =
        httpClient.execute(polishLeagueApi.getMatch(matchId)).parseHtml(htmlToMatchReportId::map)

    override suspend fun getMatchReport(matchReportId: MatchReportId, tour: TourYear): NetworkResult<MatchReport> =
        matchResponseStorage.get(matchReportId, tour)
            ?.let(matchResponseToMatchReportMapper::map)
            ?.let(Result.Companion::success)
            ?: matchReportEndpoint.getMatchReport(matchReportId, tour).onSuccess {
                matchResponseStorage.save(it, tour)
            }.map(matchResponseToMatchReportMapper::map)

    override suspend fun getPlayerDetails(tour: TourYear, playerId: PlayerId): NetworkResult<PlayerDetails> =
        httpClient.execute(polishLeagueApi.getPlayerDetails(tour, playerId)).parseHtml(htmlToPlayerDetailsMapper::map)

    private fun <T> NetworkResult<String>.parseHtml(parser: (String) -> ParseResult<T>): NetworkResult<T> =
        when (this) {
            is Result.Success -> when (val result = parser(value)) {
                is Result.Success -> Result.success(result.value)
                is Result.Failure -> {
                    parseErrorHandler.handle(result.error)
                    Result.failure(NetworkError.UnexpectedException(result.error.exception))
                }
            }
            is Result.Failure -> this
        }
}