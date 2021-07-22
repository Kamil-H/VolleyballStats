package com.kamilh.repository.polishleague

import com.kamilh.models.*
import com.kamilh.repository.HttpClient
import com.kamilh.repository.httpClientOf
import com.kamilh.repository.models.MatchResponse
import com.kamilh.repository.models.mappers.HtmlMapper
import com.kamilh.repository.models.mappers.MatchResponseToMatchReportMapper
import com.kamilh.repository.models.matchResponseOf
import com.kamilh.repository.parsing.ParseErrorHandler
import kotlinx.coroutines.runBlocking
import org.junit.Test
import repository.parsing.ParseError
import repository.parsing.ParseResult

class HttpPolishLeagueRepositoryTest {

    private fun <T> httpPolishLeagueRepositoryOf(
        httpClient: HttpClient = httpClientOf<T>(networkFailureOf(networkErrorOf())),
        polishLeagueApi: PolishLeagueApi = PolishLeagueApi(),
        htmlToTeamMapper: HtmlMapper<List<Team>> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        htmlToPlayerMapper: HtmlMapper<List<Player>> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        htmlToAllMatchesItemMapper: HtmlMapper<List<AllMatchesItem>> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        htmlToMatchReportId: HtmlMapper<MatchReportId> = htmlMapperOf(parseFailureOf(htmlParseErrorOf())),
        matchReportEndpoint: MatchReportEndpoint = matchReportEndpointOf(networkFailureOf(networkErrorOf())),
        parseErrorHandler: ParseErrorHandler = ParseErrorHandler {  },
        matchResponseStorage: MatchResponseStorage = matchResponseStorageOf(),
        matchResponseToMatchReportMapper: MatchResponseToMatchReportMapper = MatchResponseToMatchReportMapper(),
    ): HttpPolishLeagueRepository =
        HttpPolishLeagueRepository(
            httpClient = httpClient,
            polishLeagueApi = polishLeagueApi,
            htmlToTeamMapper = htmlToTeamMapper,
            htmlToPlayerMapper = htmlToPlayerMapper,
            htmlToAllMatchesItemMapper = htmlToAllMatchesItemMapper,
            htmlToMatchReportId = htmlToMatchReportId,
            matchReportEndpoint = matchReportEndpoint,
            parseErrorHandler = parseErrorHandler,
            matchResponseStorage = matchResponseStorage,
            matchResponseToMatchReportMapper = matchResponseToMatchReportMapper,
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
        ).getAllTeams(tour = tourOf())

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
        ).getAllTeams(tour = tourOf())

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
        ).getAllTeams(tour = tourOf())

        // THEN
        require(result is Result.Failure)
        require(result.error is NetworkError.UnexpectedException)
        val unexpectedException = result.error as NetworkError.UnexpectedException
        assert(unexpectedException.throwable == parseError.exception)
        assert(parseErrorToHandle == parseError)
    }

    @Test
    fun `test that when httpClient getAllPlayers returns Success and mapper returns Success, Success is getting returned`() = runBlocking {
        // GIVEN
        val parseResult = emptyList<Player>()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseSuccessOf(parseResult))

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToPlayerMapper = mapperResult,
        ).getAllPlayers(tour = tourOf())

        // THEN
        require(result is Result.Success)
        assert(result.value == parseResult)
    }

    @Test
    fun `test that when httpClient getAllPlayers returns Failure and mapper returns Success, Failure is getting returned`() = runBlocking {
        // GIVEN
        val networkError = networkErrorOf()
        val networkResult = networkFailureOf<String>(networkError)
        val mapperResult = htmlMapperOf(parseSuccessOf(emptyList<Player>()))

        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToPlayerMapper = mapperResult,
        ).getAllPlayers(tour = tourOf())

        // THEN
        require(result is Result.Failure)
        assert(result.error == networkError)
    }

    @Test
    fun `test that when httpClient getAllPlayers returns Success and mapper returns Failure, Failure is getting returned`() = runBlocking {
        // GIVEN
        val parseError = htmlParseErrorOf()
        val networkResult = networkSuccessOf("")
        val mapperResult = htmlMapperOf(parseFailureOf<List<Player>>(parseError))

        var parseErrorToHandle: ParseError? = null
        // WHEN
        val result = httpPolishLeagueRepositoryOf<String>(
            httpClient = httpClientOf(networkResult),
            htmlToPlayerMapper = mapperResult,
            parseErrorHandler = { parseErrorToHandle = it },
        ).getAllPlayers(tour = tourOf())

        // THEN
        require(result is Result.Failure)
        require(result.error is NetworkError.UnexpectedException)
        val unexpectedException = result.error as NetworkError.UnexpectedException
        assert(unexpectedException.throwable == parseError.exception)
        assert(parseErrorToHandle == parseError)
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
        ).getAllMatches(tour = tourOf())

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
        ).getAllMatches(tour = tourOf())

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
        ).getAllMatches(tour = tourOf())

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
        val tour = tourOf()
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
        ).getMatchReport(matchReportId = matchReportId, tour = tour)

        // THEN
        require(result is Result.Success)
        assert(savedResponse != null)
    }

    @Test
    fun `test that when MatchReportEndpoint returns Success and MatchResponseStorage is not empty then saved value is returned`() = runBlocking {
        // GIVEN
        val matchReportId = matchReportIdOf()
        val tour = tourOf()
        val matchReportEndpoint = matchReportEndpointOf(networkSuccessOf(matchResponseOf()))
        val savedResponse = matchResponseOf()
        val matchResponseStorage = matchResponseStorageOf(get = savedResponse)

        // WHEN
        val result = httpPolishLeagueRepositoryOf<Unit>(
            matchReportEndpoint = matchReportEndpoint,
            matchResponseStorage = matchResponseStorage,
        ).getMatchReport(matchReportId = matchReportId, tour = tour)

        // THEN
        require(result is Result.Success)
    }

    @Test
    fun `test that when MatchReportEndpoint returns Failure and MatchResponseStorage is not empty then saved value is returned`() = runBlocking {
        // GIVEN
        val matchReportId = matchReportIdOf()
        val tour = tourOf()
        val matchReportEndpoint = matchReportEndpointOf(networkFailureOf(networkErrorOf()))
        val savedResponse = matchResponseOf()
        val matchResponseStorage = matchResponseStorageOf(get = savedResponse)

        // WHEN
        val result = httpPolishLeagueRepositoryOf<Unit>(
            matchReportEndpoint = matchReportEndpoint,
            matchResponseStorage = matchResponseStorage,
        ).getMatchReport(matchReportId = matchReportId, tour = tour)

        // THEN
        require(result is Result.Success)
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

fun tourOf(tour: Int = 2020): Tour = Tour.create(tour)

fun matchReportEndpointOf(result: NetworkResult<MatchResponse>): MatchReportEndpoint =
    object : MatchReportEndpoint {
        override suspend fun getMatchReport(matchReportId: MatchReportId, tour: Tour): NetworkResult<MatchResponse> = result
    }