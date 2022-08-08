package com.kamilh.volleyballstats.repository.polishleague

import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.models.*
import com.kamilh.volleyballstats.network.HttpClient
import com.kamilh.volleyballstats.network.NetworkError
import com.kamilh.volleyballstats.network.NetworkResult
import com.kamilh.volleyballstats.network.client.httpClientOf
import com.kamilh.volleyballstats.network.result.networkFailureOf
import com.kamilh.volleyballstats.network.result.networkSuccessOf
import com.kamilh.volleyballstats.repository.models.MatchResponse
import com.kamilh.volleyballstats.repository.models.mappers.HtmlMapper
import com.kamilh.volleyballstats.repository.models.mappers.MatchResponseToMatchReportMapper
import com.kamilh.volleyballstats.repository.models.matchResponseOf
import com.kamilh.volleyballstats.repository.parsing.ParseError
import com.kamilh.volleyballstats.repository.parsing.ParseErrorHandler
import com.kamilh.volleyballstats.repository.parsing.ParseResult
import com.kamilh.volleyballstats.utils.cache.Cache
import com.kamilh.volleyballstats.utils.cache.cacheOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HttpPolishLeagueRepositoryTest {

    private fun <T> httpPolishLeagueRepositoryOf(
        httpClient: HttpClient = httpClientOf<T>(networkFailureOf(networkErrorOf())),
        polishLeagueApi: PolishLeagueApi = PolishLeagueApi(),
        htmlToTeamMapper: HtmlMapper<List<Team>> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        htmlToPlayerSnapshotMapper: HtmlMapper<List<PlayerSnapshot>> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        htmlToTeamPlayerMapper: HtmlMapper<List<TeamPlayer>> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        htmlToMatchInfoMapper: HtmlMapper<List<MatchInfo>> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        htmlToMatchReportId: HtmlMapper<MatchReportId> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        htmlToPlayerDetailsMapper: HtmlMapper<PlayerDetails> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        htmlToPlayerWithDetailsMapper: HtmlMapper<PlayerWithDetails> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        matchReportEndpoint: MatchReportEndpoint = matchReportEndpointOf(networkFailureOf(networkErrorOf())),
        parseErrorHandler: ParseErrorHandler = ParseErrorHandler {  },
        matchResponseStorage: MatchResponseStorage = matchResponseStorageOf(),
        matchResponseToMatchReportMapper: MatchResponseToMatchReportMapper = MatchResponseToMatchReportMapper(),
        tourCache: TourCache = tourCacheOf(),
        allPlayersCache: Cache<Unit, List<PlayerSnapshot>> = cacheOf(),
        allPlayersByTourCache: Cache<Season, List<TeamPlayer>> = cacheOf(),
    ): HttpPolishLeagueRepository =
        HttpPolishLeagueRepository(
            httpClient = httpClient,
            polishLeagueApi = polishLeagueApi,
            htmlToTeamMapper = htmlToTeamMapper,
            htmlToPlayerSnapshotMapper = htmlToPlayerSnapshotMapper,
            htmlToTeamPlayerMapper = htmlToTeamPlayerMapper,
            htmlToMatchInfoMapper = htmlToMatchInfoMapper,
            htmlToPlayerWithDetailsMapper = htmlToPlayerWithDetailsMapper,
            htmlToMatchReportId = htmlToMatchReportId,
            matchReportEndpoint = matchReportEndpoint,
            parseErrorHandler = parseErrorHandler,
            matchResponseStorage = matchResponseStorage,
            matchResponseToMatchReportMapper = matchResponseToMatchReportMapper,
            htmlToPlayerDetailsMapper = htmlToPlayerDetailsMapper,
            tourCache = tourCache,
            allPlayersCache = allPlayersCache,
            allPlayersByTourCache = allPlayersByTourCache,
        )

    @Test
    fun `test that when httpClient getAllTeams returns Success and mapper returns Success, Success is getting returned`() = runTest {
        // GIVEN
        val parseResult = emptyList<Team>()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseSuccessOf(parseResult))

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToTeamMapper = mapperResult,
        ).getAllTeams(season = seasonOf())

        // THEN
        require(result is Result.Success)
        assert(result.value == parseResult)
    }

    @Test
    fun `test that when httpClient getAllTeams returns Failure and mapper returns Success, Failure is getting returned`() = runTest {
        // GIVEN
        val networkError = networkErrorOf()
        val networkResult = networkFailureOf<String>(networkError)
        val mapperResult = htmlMapperOf(parseSuccessOf(emptyList<Team>()))

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToTeamMapper = mapperResult,
        ).getAllTeams(season = seasonOf())

        // THEN
        require(result is Result.Failure)
        assert(result.error == networkError)
    }

    @Test
    fun `test that when httpClient getAllTeams returns Success and mapper returns Failure, Failure is getting returned`() = runTest {
        // GIVEN
        val parseError = htmlParseErrorOf()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseFailureOf<List<Team>>(parseError))

        var parseErrorToHandle: ParseError? = null
        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToTeamMapper = mapperResult,
            parseErrorHandler = { parseErrorToHandle = it },
        ).getAllTeams(season = seasonOf())

        // THEN
        require(result is Result.Failure)
        require(result.error is NetworkError.UnexpectedException)
        val unexpectedException = result.error as NetworkError.UnexpectedException
        assert(unexpectedException.throwable == parseError.exception)
        assert(parseErrorToHandle == parseError)
    }

    @Test
    fun `getAllPlayers by tour returns value from cache when it's available`() = runTest {
        // GIVEN
        val cached = listOf(teamPlayerOf())

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            allPlayersByTourCache = cacheOf(get = cached),
        ).getAllPlayers(season = seasonOf())

        // THEN
        require(result is Result.Success)
        assert(result.value == cached)
    }

    @Test
    fun `test that when httpClient getAllPlayers by tour returns Success and mapper returns Success, Success is getting returned`() = runTest {
        // GIVEN
        val parseResult = emptyList<TeamPlayer>()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseSuccessOf(parseResult))
        var setCacheCalled = false

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToTeamPlayerMapper = mapperResult,
            allPlayersByTourCache = cacheOf { _, _ -> setCacheCalled = true },
        ).getAllPlayers(season = seasonOf())

        // THEN
        require(result is Result.Success)
        assert(result.value == parseResult)
        assertTrue(setCacheCalled)
    }

    @Test
    fun `test that when httpClient getAllPlayers by tour returns Failure and mapper returns Success, Failure is getting returned`() = runTest {
        // GIVEN
        val networkError = networkErrorOf()
        val networkResult = networkFailureOf<String>(networkError)
        val mapperResult = htmlMapperOf(parseSuccessOf(emptyList<TeamPlayer>()))
        var setCacheCalled = false

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToTeamPlayerMapper = mapperResult,
            allPlayersByTourCache = cacheOf { _, _ -> setCacheCalled = true },
        ).getAllPlayers(season = seasonOf())

        // THEN
        require(result is Result.Failure)
        assert(result.error == networkError)
        assertFalse(setCacheCalled)
    }

    @Test
    fun `test that when httpClient getAllPlayers by tour returns Success and mapper returns Failure, Failure is getting returned`() = runTest {
        // GIVEN
        val parseError = htmlParseErrorOf()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseFailureOf<List<TeamPlayer>>(parseError))
        var setCacheCalled = false

        var parseErrorToHandle: ParseError? = null
        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToTeamPlayerMapper = mapperResult,
            parseErrorHandler = { parseErrorToHandle = it },
            allPlayersByTourCache = cacheOf { _, _ -> setCacheCalled = true },
        ).getAllPlayers(season = seasonOf())

        // THEN
        require(result is Result.Failure)
        require(result.error is NetworkError.UnexpectedException)
        val unexpectedException = result.error as NetworkError.UnexpectedException
        assert(unexpectedException.throwable == parseError.exception)
        assert(parseErrorToHandle == parseError)
        assertFalse(setCacheCalled)
    }

    @Test
    fun `getAllPlayers returns value from cache when it's available`() = runTest {
        // GIVEN
        val cached = listOf(playerSnapshotOf())

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            allPlayersCache = cacheOf(get = cached),
        ).getAllPlayers()

        // THEN
        require(result is Result.Success)
        assert(result.value == cached)
    }

    @Test
    fun `test that when httpClient getAllPlayers returns Success and mapper returns Success, Success is getting returned`() = runTest {
        // GIVEN
        val parseResult = emptyList<PlayerSnapshot>()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseSuccessOf(parseResult))
        var setCacheCalled = false

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToPlayerSnapshotMapper = mapperResult,
            allPlayersCache = cacheOf { _, _ -> setCacheCalled = true },
        ).getAllPlayers()

        // THEN
        require(result is Result.Success)
        assert(result.value == parseResult)
        assertTrue(setCacheCalled)
    }

    @Test
    fun `test that when httpClient getAllPlayers returns Failure and mapper returns Success, Failure is getting returned`() = runTest {
        // GIVEN
        val networkError = networkErrorOf()
        val networkResult = networkFailureOf<String>(networkError)
        val mapperResult = htmlMapperOf(parseSuccessOf(emptyList<PlayerSnapshot>()))
        var setCacheCalled = false

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToPlayerSnapshotMapper = mapperResult,
            allPlayersCache = cacheOf { _, _ -> setCacheCalled = true },
        ).getAllPlayers()

        // THEN
        require(result is Result.Failure)
        assert(result.error == networkError)
        assertFalse(setCacheCalled)
    }

    @Test
    fun `test that when httpClient getAllPlayers returns Success and mapper returns Failure, Failure is getting returned`() = runTest {
        // GIVEN
        val parseError = htmlParseErrorOf()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseFailureOf<List<PlayerSnapshot>>(parseError))
        var setCacheCalled = false

        var parseErrorToHandle: ParseError? = null
        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToPlayerSnapshotMapper = mapperResult,
            parseErrorHandler = { parseErrorToHandle = it },
            allPlayersCache = cacheOf { _, _ -> setCacheCalled = true },
        ).getAllPlayers()

        // THEN
        require(result is Result.Failure)
        require(result.error is NetworkError.UnexpectedException)
        val unexpectedException = result.error as NetworkError.UnexpectedException
        assert(unexpectedException.throwable == parseError.exception)
        assert(parseErrorToHandle == parseError)
        assertFalse(setCacheCalled)
    }

    @Test
    fun `test that when httpClient getAllMatches returns Success and mapper returns Success, Success is getting returned`() = runTest {
        // GIVEN
        val parseResult = emptyList<MatchInfo>()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseSuccessOf(parseResult))

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToMatchInfoMapper = mapperResult,
        ).getAllMatches(season = seasonOf())

        // THEN
        require(result is Result.Success)
        assert(result.value == parseResult)
    }

    @Test
    fun `test that when httpClient getAllMatches returns Failure and mapper returns Success, Failure is getting returned`() = runTest {
        // GIVEN
        val networkError = networkErrorOf()
        val networkResult = networkFailureOf<String>(networkError)
        val mapperResult = htmlMapperOf(parseSuccessOf(emptyList<MatchInfo>()))

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToMatchInfoMapper = mapperResult,
        ).getAllMatches(season = seasonOf())

        // THEN
        require(result is Result.Failure)
        assert(result.error == networkError)
    }

    @Test
    fun `test that when httpClient getAllMatches returns Success and mapper returns Failure, Failure is getting returned`() = runTest {
        // GIVEN
        val parseError = htmlParseErrorOf()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseFailureOf<List<MatchInfo>>(parseError))

        var parseErrorToHandle: ParseError? = null
        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToMatchInfoMapper = mapperResult,
            parseErrorHandler = { parseErrorToHandle = it },
        ).getAllMatches(season = seasonOf())

        // THEN
        require(result is Result.Failure)
        require(result.error is NetworkError.UnexpectedException)
        val unexpectedException = result.error as NetworkError.UnexpectedException
        assert(unexpectedException.throwable == parseError.exception)
        assert(parseErrorToHandle == parseError)
    }

    @Test
    fun `test that when httpClient getMatchReportId returns Success and mapper returns Success, Success is getting returned`() = runTest {
        // GIVEN
        val parseResult = matchReportIdOf()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseSuccessOf(parseResult))

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToMatchReportId = mapperResult,
        ).getMatchReportId(matchId = matchIdOf())

        // THEN
        require(result is Result.Success)
        assert(result.value == parseResult)
    }

    @Test
    fun `test that when httpClient getMatchReportId returns Failure and mapper returns Success, Failure is getting returned`() = runTest {
        // GIVEN
        val networkError = networkErrorOf()
        val networkResult = networkFailureOf<String>(networkError)
        val mapperResult = htmlMapperOf(parseSuccessOf(matchReportIdOf()))

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToMatchReportId = mapperResult,
        ).getMatchReportId(matchId = matchIdOf())

        // THEN
        require(result is Result.Failure)
        assert(result.error == networkError)
    }

    @Test
    fun `test that when httpClient getMatchReportId returns Success and mapper returns Failure, Failure is getting returned`() = runTest {
        // GIVEN
        val parseError = htmlParseErrorOf()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseFailureOf<MatchReportId>(parseError))

        var parseErrorToHandle: ParseError? = null
        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToMatchReportId = mapperResult,
            parseErrorHandler = { parseErrorToHandle = it },
        ).getMatchReportId(matchId = matchIdOf())

        // THEN
        require(result is Result.Failure)
        require(result.error is NetworkError.UnexpectedException)
        val unexpectedException = result.error as NetworkError.UnexpectedException
        assert(unexpectedException.throwable == parseError.exception)
        assert(parseErrorToHandle == parseError)
    }

    @Test
    fun `test that when MatchReportEndpoint returns Success and MatchResponseStorage is empty then Success is returned and response is getting saved`() = runTest {
        // GIVEN
        val matchReportId = matchReportIdOf()
        val tour = seasonOf()
        val matchResponse = matchResponseOf()
        val matchReportEndpoint = matchReportEndpointOf(networkSuccessOf(matchResponse))
        var savedResponse: MatchResponse? = null
        val matchResponseStorage = matchResponseStorageOf(
            get = null,
            saveCallback = { saveMatchResponse, _ ->
                savedResponse = saveMatchResponse
            }
        )

        // WHEN
        val result = httpPolishLeagueRepositoryOf<Unit>(
            matchReportEndpoint = matchReportEndpoint,
            matchResponseStorage = matchResponseStorage,
        ).getMatchReport(matchReportId = matchReportId, season = tour)

        // THEN
        require(result is Result.Success)
        assert(savedResponse != null)
    }

    @Test
    fun `test that when MatchReportEndpoint returns Success and MatchResponseStorage is not empty then saved value is returned`() = runTest {
        // GIVEN
        val matchReportId = matchReportIdOf()
        val tour = seasonOf()
        val matchReportEndpoint = matchReportEndpointOf(networkSuccessOf(matchResponseOf()))
        val savedResponse = matchResponseOf()
        val matchResponseStorage = matchResponseStorageOf(get = savedResponse)

        // WHEN
        val result = httpPolishLeagueRepositoryOf<Unit>(
            matchReportEndpoint = matchReportEndpoint,
            matchResponseStorage = matchResponseStorage,
        ).getMatchReport(matchReportId = matchReportId, season = tour)

        // THEN
        require(result is Result.Success)
    }

    @Test
    fun `test that when MatchReportEndpoint returns Failure and MatchResponseStorage is not empty then saved value is returned`() = runTest {
        // GIVEN
        val matchReportId = matchReportIdOf()
        val tour = seasonOf()
        val matchReportEndpoint = matchReportEndpointOf(networkFailureOf(networkErrorOf()))
        val savedResponse = matchResponseOf()
        val matchResponseStorage = matchResponseStorageOf(get = savedResponse)

        // WHEN
        val result = httpPolishLeagueRepositoryOf<Unit>(
            matchReportEndpoint = matchReportEndpoint,
            matchResponseStorage = matchResponseStorage,
        ).getMatchReport(matchReportId = matchReportId, season = tour)

        // THEN
        require(result is Result.Success)
    }

    @Test
    fun `test that when httpClient getPlayerDetails returns Success and mapper returns Success, Success is getting returned`() = runTest {
        // GIVEN
        val parseResult = playerDetailsOf()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseSuccessOf(parseResult))

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToPlayerDetailsMapper = mapperResult,
        ).getPlayerDetails(season = seasonOf(), playerId = playerIdOf())

        // THEN
        require(result is Result.Success)
        assert(result.value == parseResult)
    }

    @Test
    fun `test that when httpClient getPlayerDetails returns Failure and mapper returns Success, Failure is getting returned`() = runTest {
        // GIVEN
        val networkError = networkErrorOf()
        val networkResult = networkFailureOf<String>(networkError)
        val mapperResult = htmlMapperOf(parseSuccessOf(playerDetailsOf()))

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToPlayerDetailsMapper = mapperResult,
        ).getPlayerDetails(season = seasonOf(), playerId = playerIdOf())

        // THEN
        require(result is Result.Failure)
        assert(result.error == networkError)
    }

    @Test
    fun `test that when httpClient getPlayerDetails returns Success and mapper returns Failure, Failure is getting returned`() = runTest {
        // GIVEN
        val parseError = htmlParseErrorOf()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseFailureOf<PlayerDetails>(parseError))

        var parseErrorToHandle: ParseError? = null
        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToPlayerDetailsMapper = mapperResult,
            parseErrorHandler = { parseErrorToHandle = it },
        ).getPlayerDetails(season = seasonOf(), playerId = playerIdOf())

        // THEN
        require(result is Result.Failure)
        require(result.error is NetworkError.UnexpectedException)
        val unexpectedException = result.error as NetworkError.UnexpectedException
        assert(unexpectedException.throwable == parseError.exception)
        assert(parseErrorToHandle == parseError)
    }

    @Test
    fun `test that when httpClient getPlayerWithDetails returns Success and mapper returns Success, Success is getting returned`() = runTest {
        // GIVEN
        val parseResult = playerWithDetailsOf()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseSuccessOf(parseResult))

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToPlayerWithDetailsMapper = mapperResult,
        ).getPlayerWithDetails(season = seasonOf(), playerId = playerIdOf())

        // THEN
        require(result is Result.Success)
        assert(result.value == parseResult)
    }

    @Test
    fun `test that when httpClient getPlayerWithDetails returns Failure and mapper returns Success, Failure is getting returned`() = runTest {
        // GIVEN
        val networkError = networkErrorOf()
        val networkResult = networkFailureOf<String>(networkError)
        val mapperResult = htmlMapperOf(parseSuccessOf(playerWithDetailsOf()))

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToPlayerWithDetailsMapper = mapperResult,
        ).getPlayerWithDetails(season = seasonOf(), playerId = playerIdOf())

        // THEN
        require(result is Result.Failure)
        assert(result.error == networkError)
    }

    @Test
    fun `test that when httpClient getPlayerWithDetails returns Success and mapper returns Failure, Failure is getting returned`() = runTest {
        // GIVEN
        val parseError = htmlParseErrorOf()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseFailureOf<PlayerWithDetails>(parseError))

        var parseErrorToHandle: ParseError? = null
        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToPlayerWithDetailsMapper = mapperResult,
            parseErrorHandler = { parseErrorToHandle = it },
        ).getPlayerWithDetails(season = seasonOf(), playerId = playerIdOf())

        // THEN
        require(result is Result.Failure)
        require(result.error is NetworkError.UnexpectedException)
        val unexpectedException = result.error as NetworkError.UnexpectedException
        assert(unexpectedException.throwable == parseError.exception)
        assert(parseErrorToHandle == parseError)
    }

    @Test
    fun `test that getTours returns value from the cache`() = runTest {
        // GIVEN
        val cachedValue = listOf(tourOf())

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            tourCache = tourCacheOf(cachedValue)
        ).getAllTours()

        // THEN
        require(result is Result.Success)
        assert(result.value == cachedValue)
    }
}

fun htmlParseErrorOf(
    content: String = "",
    exception: Exception = IllegalStateException(),
): ParseError.Html = ParseError.Html(content, exception)

fun jsonParseErrorOf(
    content: String = "",
    exception: Exception = IllegalStateException(),
): ParseError.Json = ParseError.Json(content, exception)

fun <T> htmlMapperOf(result: ParseResult<T>): HtmlMapper<T> = HtmlMapper { result }

fun matchReportEndpointOf(result: NetworkResult<MatchResponse>): MatchReportEndpoint =
    object : MatchReportEndpoint {
        override suspend fun getMatchReport(matchReportId: MatchReportId, tour: Season): NetworkResult<MatchResponse> = result
    }

fun tourCacheOf(
    getAll: List<Tour> = emptyList(),
): TourCache = object : TourCache {
    override fun getAll(): List<Tour> = getAll
}

fun polishLeagueRepositoryOf(
    getAllTeams: NetworkResult<List<Team>> = networkFailureOf(networkErrorOf()),
    getAllPlayersByTour: NetworkResult<List<TeamPlayer>> = networkFailureOf(networkErrorOf()),
    getAllPlayers: NetworkResult<List<PlayerSnapshot>> = networkFailureOf(networkErrorOf()),
    getAllMatches: NetworkResult<List<MatchInfo>> = networkFailureOf(networkErrorOf()),
    getMatchReportId: NetworkResult<MatchReportId> = networkFailureOf(networkErrorOf()),
    getMatchReport: NetworkResult<RawMatchReport> = networkFailureOf(networkErrorOf()),
    getPlayerDetails: NetworkResult<PlayerDetails> = networkFailureOf(networkErrorOf()),
    getPlayerWithDetails: NetworkResult<PlayerWithDetails> = networkFailureOf(networkErrorOf()),
    getAllTours: NetworkResult<List<Tour>> = networkFailureOf(networkErrorOf()),
): PolishLeagueRepository = object : PolishLeagueRepository {
    override suspend fun getAllTeams(season: Season): NetworkResult<List<Team>> = getAllTeams
    override suspend fun getAllPlayers(season: Season): NetworkResult<List<TeamPlayer>> = getAllPlayersByTour
    override suspend fun getAllPlayers(): NetworkResult<List<PlayerSnapshot>> = getAllPlayers
    override suspend fun getAllMatches(season: Season): NetworkResult<List<MatchInfo>> = getAllMatches
    override suspend fun getMatchReportId(matchId: MatchId): NetworkResult<MatchReportId> = getMatchReportId
    override suspend fun getMatchReport(matchReportId: MatchReportId, season: Season): NetworkResult<RawMatchReport> = getMatchReport
    override suspend fun getPlayerDetails(season: Season, playerId: PlayerId): NetworkResult<PlayerDetails> = getPlayerDetails
    override suspend fun getPlayerWithDetails(season: Season, playerId: PlayerId): NetworkResult<PlayerWithDetails> = getPlayerWithDetails
    override suspend fun getAllTours(): NetworkResult<List<Tour>> = getAllTours
}