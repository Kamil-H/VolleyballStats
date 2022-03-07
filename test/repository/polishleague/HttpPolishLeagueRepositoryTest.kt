package com.kamilh.repository.polishleague

import com.kamilh.models.*
import com.kamilh.repository.HttpClient
import com.kamilh.repository.httpClientOf
import com.kamilh.repository.models.MatchResponse
import com.kamilh.repository.models.mappers.HtmlMapper
import com.kamilh.repository.models.mappers.MatchResponseToMatchReportMapper
import com.kamilh.repository.models.matchResponseOf
import com.kamilh.repository.parsing.ParseErrorHandler
import com.kamilh.utils.cache.Cache
import com.kamilh.utils.cache.cacheOf
import kotlinx.coroutines.runBlocking
import models.PlayerWithDetails
import org.junit.Test
import repository.parsing.ParseError
import repository.parsing.ParseResult
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HttpPolishLeagueRepositoryTest {

    private fun <T> httpPolishLeagueRepositoryOf(
        httpClient: HttpClient = httpClientOf<T>(networkFailureOf(networkErrorOf())),
        polishLeagueApi: PolishLeagueApi = PolishLeagueApi(),
        htmlToTeamMapper: HtmlMapper<List<Team>> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        htmlToPlayerMapper: HtmlMapper<List<Player>> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        htmlToTeamPlayerMapper: HtmlMapper<List<TeamPlayer>> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        htmlToAllMatchesItemMapper: HtmlMapper<List<AllMatchesItem>> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        htmlToMatchReportId: HtmlMapper<MatchReportId> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        htmlToPlayerDetailsMapper: HtmlMapper<PlayerDetails> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        htmlToPlayerWithDetailsMapper: HtmlMapper<PlayerWithDetails> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        matchReportEndpoint: MatchReportEndpoint = matchReportEndpointOf(networkFailureOf(networkErrorOf())),
        parseErrorHandler: ParseErrorHandler = ParseErrorHandler {  },
        matchResponseStorage: MatchResponseStorage = matchResponseStorageOf(),
        matchResponseToMatchReportMapper: MatchResponseToMatchReportMapper = MatchResponseToMatchReportMapper(),
        tourCache: TourCache = tourCacheOf(),
        allPlayersCache: Cache<Unit, List<Player>> = cacheOf(),
        allPlayersByTourCache: Cache<Season, List<TeamPlayer>> = cacheOf(),
    ): HttpPolishLeagueRepository =
        HttpPolishLeagueRepository(
            httpClient = httpClient,
            polishLeagueApi = polishLeagueApi,
            htmlToTeamMapper = htmlToTeamMapper,
            htmlToPlayerMapper = htmlToPlayerMapper,
            htmlToTeamPlayerMapper = htmlToTeamPlayerMapper,
            htmlToAllMatchesItemMapper = htmlToAllMatchesItemMapper,
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
    fun `test that when httpClient getAllTeams returns Success and mapper returns Success, Success is getting returned`() = runBlocking {
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
    fun `test that when httpClient getAllTeams returns Failure and mapper returns Success, Failure is getting returned`() = runBlocking {
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
    fun `test that when httpClient getAllTeams returns Success and mapper returns Failure, Failure is getting returned`() = runBlocking {
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
    fun `getAllPlayers by tour returns value from cache when it's available`() = runBlocking {
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
    fun `test that when httpClient getAllPlayers by tour returns Success and mapper returns Success, Success is getting returned`() = runBlocking {
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
    fun `test that when httpClient getAllPlayers by tour returns Failure and mapper returns Success, Failure is getting returned`() = runBlocking {
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
    fun `test that when httpClient getAllPlayers by tour returns Success and mapper returns Failure, Failure is getting returned`() = runBlocking {
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
    fun `getAllPlayers returns value from cache when it's available`() = runBlocking {
        // GIVEN
        val cached = listOf(playerOf())

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            allPlayersCache = cacheOf(get = cached),
        ).getAllPlayers()

        // THEN
        require(result is Result.Success)
        assert(result.value == cached)
    }

    @Test
    fun `test that when httpClient getAllPlayers returns Success and mapper returns Success, Success is getting returned`() = runBlocking {
        // GIVEN
        val parseResult = emptyList<Player>()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseSuccessOf(parseResult))
        var setCacheCalled = false

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToPlayerMapper = mapperResult,
            allPlayersCache = cacheOf { _, _ -> setCacheCalled = true },
        ).getAllPlayers()

        // THEN
        require(result is Result.Success)
        assert(result.value == parseResult)
        assertTrue(setCacheCalled)
    }

    @Test
    fun `test that when httpClient getAllPlayers returns Failure and mapper returns Success, Failure is getting returned`() = runBlocking {
        // GIVEN
        val networkError = networkErrorOf()
        val networkResult = networkFailureOf<String>(networkError)
        val mapperResult = htmlMapperOf(parseSuccessOf(emptyList<Player>()))
        var setCacheCalled = false

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToPlayerMapper = mapperResult,
            allPlayersCache = cacheOf { _, _ -> setCacheCalled = true },
        ).getAllPlayers()

        // THEN
        require(result is Result.Failure)
        assert(result.error == networkError)
        assertFalse(setCacheCalled)
    }

    @Test
    fun `test that when httpClient getAllPlayers returns Success and mapper returns Failure, Failure is getting returned`() = runBlocking {
        // GIVEN
        val parseError = htmlParseErrorOf()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseFailureOf<List<Player>>(parseError))
        var setCacheCalled = false

        var parseErrorToHandle: ParseError? = null
        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToPlayerMapper = mapperResult,
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
    fun `test that when httpClient getAllMatches returns Success and mapper returns Success, Success is getting returned`() = runBlocking {
        // GIVEN
        val parseResult = emptyList<AllMatchesItem>()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseSuccessOf(parseResult))

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToAllMatchesItemMapper = mapperResult,
        ).getAllMatches(season = seasonOf())

        // THEN
        require(result is Result.Success)
        assert(result.value == parseResult)
    }

    @Test
    fun `test that when httpClient getAllMatches returns Failure and mapper returns Success, Failure is getting returned`() = runBlocking {
        // GIVEN
        val networkError = networkErrorOf()
        val networkResult = networkFailureOf<String>(networkError)
        val mapperResult = htmlMapperOf(parseSuccessOf(emptyList<AllMatchesItem>()))

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToAllMatchesItemMapper = mapperResult,
        ).getAllMatches(season = seasonOf())

        // THEN
        require(result is Result.Failure)
        assert(result.error == networkError)
    }

    @Test
    fun `test that when httpClient getAllMatches returns Success and mapper returns Failure, Failure is getting returned`() = runBlocking {
        // GIVEN
        val parseError = htmlParseErrorOf()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseFailureOf<List<AllMatchesItem>>(parseError))

        var parseErrorToHandle: ParseError? = null
        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToAllMatchesItemMapper = mapperResult,
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
    fun `test that when httpClient getMatchReportId returns Success and mapper returns Success, Success is getting returned`() = runBlocking {
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
    fun `test that when httpClient getMatchReportId returns Failure and mapper returns Success, Failure is getting returned`() = runBlocking {
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
    fun `test that when httpClient getMatchReportId returns Success and mapper returns Failure, Failure is getting returned`() = runBlocking {
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
    fun `test that when MatchReportEndpoint returns Success and MatchResponseStorage is empty then Success is returned and response is getting saved`() = runBlocking {
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
    fun `test that when MatchReportEndpoint returns Success and MatchResponseStorage is not empty then saved value is returned`() = runBlocking {
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
    fun `test that when MatchReportEndpoint returns Failure and MatchResponseStorage is not empty then saved value is returned`() = runBlocking {
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
    fun `test that when httpClient getPlayerDetails returns Success and mapper returns Success, Success is getting returned`() = runBlocking {
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
    fun `test that when httpClient getPlayerDetails returns Failure and mapper returns Success, Failure is getting returned`() = runBlocking {
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
    fun `test that when httpClient getPlayerDetails returns Success and mapper returns Failure, Failure is getting returned`() = runBlocking {
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
    fun `test that when httpClient getPlayerWithDetails returns Success and mapper returns Success, Success is getting returned`() = runBlocking {
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
    fun `test that when httpClient getPlayerWithDetails returns Failure and mapper returns Success, Failure is getting returned`() = runBlocking {
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
    fun `test that when httpClient getPlayerWithDetails returns Success and mapper returns Failure, Failure is getting returned`() = runBlocking {
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
    fun `test that getTours returns value from the cache`() = runBlocking {
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

fun networkErrorOf(throwable: Throwable = Throwable()): NetworkError = NetworkError.createFrom(throwable)

fun htmlParseErrorOf(
    content: String = "",
    exception: Exception = IllegalStateException(),
): ParseError.Html = ParseError.Html(content, exception)

fun jsonParseErrorOf(
    content: String = "",
    exception: Exception = IllegalStateException(),
): ParseError.Json = ParseError.Json(content, exception)

fun <T> htmlMapperOf(result: ParseResult<T>): HtmlMapper<T> = HtmlMapper { result }

fun seasonOf(tour: Int = 2020): Season = Season.create(tour)

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
    getAllPlayers: NetworkResult<List<Player>> = networkFailureOf(networkErrorOf()),
    getAllMatches: NetworkResult<List<AllMatchesItem>> = networkFailureOf(networkErrorOf()),
    getMatchReportId: NetworkResult<MatchReportId> = networkFailureOf(networkErrorOf()),
    getMatchReport: NetworkResult<MatchReport> = networkFailureOf(networkErrorOf()),
    getPlayerDetails: NetworkResult<PlayerDetails> = networkFailureOf(networkErrorOf()),
    getPlayerWithDetails: NetworkResult<PlayerWithDetails> = networkFailureOf(networkErrorOf()),
    getAllTours: NetworkResult<List<Tour>> = networkFailureOf(networkErrorOf()),
): PolishLeagueRepository = object : PolishLeagueRepository {
    override suspend fun getAllTeams(season: Season): NetworkResult<List<Team>> = getAllTeams
    override suspend fun getAllPlayers(season: Season): NetworkResult<List<TeamPlayer>> = getAllPlayersByTour
    override suspend fun getAllPlayers(): NetworkResult<List<Player>> = getAllPlayers
    override suspend fun getAllMatches(season: Season): NetworkResult<List<AllMatchesItem>> = getAllMatches
    override suspend fun getMatchReportId(matchId: MatchId): NetworkResult<MatchReportId> = getMatchReportId
    override suspend fun getMatchReport(matchReportId: MatchReportId, season: Season): NetworkResult<MatchReport> = getMatchReport
    override suspend fun getPlayerDetails(season: Season, playerId: PlayerId): NetworkResult<PlayerDetails> = getPlayerDetails
    override suspend fun getPlayerWithDetails(season: Season, playerId: PlayerId): NetworkResult<PlayerWithDetails> = getPlayerWithDetails
    override suspend fun getAllTours(): NetworkResult<List<Tour>> = getAllTours
}