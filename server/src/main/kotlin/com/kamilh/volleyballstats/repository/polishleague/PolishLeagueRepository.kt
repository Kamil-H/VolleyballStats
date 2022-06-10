package com.kamilh.volleyballstats.repository.polishleague

import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.models.*
import com.kamilh.volleyballstats.network.HttpClient
import com.kamilh.volleyballstats.network.NetworkResult
import com.kamilh.volleyballstats.repository.models.mappers.HtmlMapper
import com.kamilh.volleyballstats.repository.models.mappers.MatchResponseToMatchReportMapper
import com.kamilh.volleyballstats.repository.parsing.ParseErrorHandler
import com.kamilh.volleyballstats.repository.parsing.ParseResult
import com.kamilh.volleyballstats.utils.cache.Cache
import me.tatarka.inject.annotations.Inject

interface PolishLeagueRepository {

    suspend fun getAllTeams(season: Season): NetworkResult<List<Team>>

    suspend fun getAllPlayers(season: Season): NetworkResult<List<TeamPlayer>>

    suspend fun getAllMatches(season: Season): NetworkResult<List<MatchInfo>>

    suspend fun getMatchReportId(matchId: MatchId): NetworkResult<MatchReportId>

    suspend fun getMatchReport(matchReportId: MatchReportId, season: Season): NetworkResult<RawMatchReport>

    suspend fun getPlayerDetails(season: Season, playerId: PlayerId): NetworkResult<PlayerDetails>

    suspend fun getPlayerWithDetails(season: Season, playerId: PlayerId): NetworkResult<PlayerWithDetails>

    suspend fun getAllTours(): NetworkResult<List<Tour>>

    suspend fun getAllPlayers(): NetworkResult<List<PlayerSnapshot>>
}

@Inject
class HttpPolishLeagueRepository(
    private val httpClient: HttpClient,
    private val polishLeagueApi: PolishLeagueApi,
    private val htmlToTeamMapper: HtmlMapper<List<Team>>,
    private val htmlToTeamPlayerMapper: HtmlMapper<List<TeamPlayer>>,
    private val htmlToPlayerSnapshotMapper: HtmlMapper<List<PlayerSnapshot>>,
    private val htmlToMatchInfoMapper: HtmlMapper<List<MatchInfo>>,
    private val htmlToMatchReportId: HtmlMapper<MatchReportId>,
    private val htmlToPlayerDetailsMapper: HtmlMapper<PlayerDetails>,
    private val htmlToPlayerWithDetailsMapper: HtmlMapper<PlayerWithDetails>,
    private val matchReportEndpoint: MatchReportEndpoint,
    private val parseErrorHandler: ParseErrorHandler,
    private val matchResponseStorage: MatchResponseStorage,
    private val matchResponseToMatchReportMapper: MatchResponseToMatchReportMapper,
    private val tourCache: TourCache,
    private val allPlayersCache: Cache<Unit, List<PlayerSnapshot>>,
    private val allPlayersByTourCache: Cache<Season, List<TeamPlayer>>,
) : PolishLeagueRepository {

    override suspend fun getAllTeams(season: Season): NetworkResult<List<Team>> =
        httpClient.execute(polishLeagueApi.getTeams(season)).parseHtml(htmlToTeamMapper::map)

    override suspend fun getAllPlayers(season: Season): NetworkResult<List<TeamPlayer>> =
        getFromCacheOrFetch(key = season, cache = allPlayersByTourCache) {
            httpClient.execute(polishLeagueApi.getPlayers(season)).parseHtml(htmlToTeamPlayerMapper::map)
        }

    override suspend fun getAllPlayers(): NetworkResult<List<PlayerSnapshot>> =
        getFromCacheOrFetch(key = Unit, cache = allPlayersCache) {
            httpClient.execute(polishLeagueApi.getAllPlayers()).parseHtml(htmlToPlayerSnapshotMapper::map)
        }

    private suspend inline fun <KEY, VALUE> getFromCacheOrFetch(
        key: KEY,
        cache: Cache<KEY, VALUE>,
        fetch: (key: KEY) -> NetworkResult<VALUE>,
    ): NetworkResult<VALUE> = cache.get(key)?.let { NetworkResult.success(it) } ?: fetch(key).onSuccess {
        cache.set(key, it)
    }

    override suspend fun getAllMatches(season: Season): NetworkResult<List<MatchInfo>> =
        httpClient.execute(polishLeagueApi.getAllMatches(season)).parseHtml(htmlToMatchInfoMapper::map)

    override suspend fun getMatchReportId(matchId: MatchId): NetworkResult<MatchReportId> =
        httpClient.execute(polishLeagueApi.getMatch(matchId)).parseHtml(htmlToMatchReportId::map)

    override suspend fun getMatchReport(matchReportId: MatchReportId, season: Season): NetworkResult<RawMatchReport> =
        matchResponseStorage.get(matchReportId, season)
            ?.let(matchResponseToMatchReportMapper::map)
            ?.let(Result.Companion::success)
            ?: matchReportEndpoint.getMatchReport(matchReportId, season).onSuccess {
                matchResponseStorage.save(it, season)
            }.map(matchResponseToMatchReportMapper::map)

    override suspend fun getPlayerDetails(season: Season, playerId: PlayerId): NetworkResult<PlayerDetails> =
        httpClient.execute(polishLeagueApi.getPlayerDetails(season, playerId)).parseHtml(htmlToPlayerDetailsMapper::map)

    override suspend fun getPlayerWithDetails(season: Season, playerId: PlayerId): NetworkResult<PlayerWithDetails> =
        httpClient.execute(polishLeagueApi.getPlayerWithDetails(season, playerId)).parseHtml(htmlToPlayerWithDetailsMapper::map)

    override suspend fun getAllTours(): NetworkResult<List<Tour>> =
        Result.success(tourCache.getAll())

    private fun <T> NetworkResult<String>.parseHtml(parser: (String) -> ParseResult<T>): NetworkResult<T> =
        when (this) {
            is Result.Success -> when (val result = parser(value)) {
                is Result.Success -> Result.success(result.value)
                is Result.Failure -> {
                    parseErrorHandler.handle(result.error)
                    Result.failure(com.kamilh.volleyballstats.network.NetworkError.UnexpectedException(result.error.exception))
                }
            }
            is Result.Failure -> this
        }
}