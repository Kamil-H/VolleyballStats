package com.kamilh.volleyballstats.repository.polishleague

import com.kamilh.volleyballstats.domain.models.Result
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Url
import com.kamilh.volleyballstats.models.MatchReportId
import com.kamilh.volleyballstats.network.NetworkError
import com.kamilh.volleyballstats.network.NetworkResult
import com.kamilh.volleyballstats.repository.models.MatchResponse
import com.kamilh.volleyballstats.repository.models.PlayByPlayResponse
import com.kamilh.volleyballstats.repository.parsing.ParseError
import com.kamilh.volleyballstats.repository.parsing.ParseErrorHandler
import com.kamilh.volleyballstats.repository.sockettoken.SocketToken
import com.kamilh.volleyballstats.repository.sockettoken.SocketTokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.ws
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

interface MatchReportEndpoint {

    suspend fun getMatchReport(matchReportId: MatchReportId, tour: Season): NetworkResult<MatchResponse>
}

@Inject
class WebSocketMatchReportEndpoint(
    private val json: Json,
    private val httpClient: HttpClient,
    private val parseErrorHandler: ParseErrorHandler,
    private val socketTokenProvider: SocketTokenProvider,
) : MatchReportEndpoint {

    override suspend fun getMatchReport(matchReportId: MatchReportId, tour: Season): NetworkResult<MatchResponse> {
        val token = socketTokenProvider.getToken()
            ?: return Result.failure(NetworkError.HttpError.UnauthorizedException)
        val url =
            Url.create("wss://api.widgets.volleystation.com/socket.io/?connectionPathName=%2Fplay-by-play%2F${matchReportId.value}&token=${token.value}&EIO=3&transport=websocket")
        val message = "420[\"find\",\"widget/play-by-play\",{\"matchId\":${matchReportId.value},\"\$limit\":1}]"

        var result: NetworkResult<MatchResponse>? = null
        httpClient.ws(urlString = url.value) {
            val frame = incoming.receive()

            val timeout = frame.text()?.dropBitsFromTheBeginning()?.let(::getTimeout) ?: DEFAULT_TIMEOUT

            delay(timeout)
            send(message)

            var text: String?
            do {
                text = incoming.receive().text()?.dropBitsFromTheBeginning()
            } while (text.isNullOrEmpty())
            val stringJson = text.dropUntilObjectDefinition()
            result = createResult(stringJson, token)
        }
        return result!!
    }

    private fun getTimeout(requestInformationResponseText: String): Long =
        try {
            val requestInformationResponse = json.decodeFromString<WebSocketRequestInformationResponse>(requestInformationResponseText)
            requestInformationResponse.pingTimeout
        } catch (exception: Exception) {
            handleException(exception, requestInformationResponseText)
            DEFAULT_TIMEOUT
        }

    private suspend fun createResult(stringJson: String, token: SocketToken): NetworkResult<MatchResponse>? =
        try {
            val playByPlayResponse = json.decodeFromString<PlayByPlayResponse>(stringJson)
            if (playByPlayResponse.data.isEmpty()) {
                error("PlayByPlayResponse.data is empty")
            }
            val matchResponse = playByPlayResponse.data.first()
            Result.success(matchResponse)
        } catch (exception: Exception) {
            try {
                json.decodeFromString<WebSocketErrorResponse>(stringJson)
                socketTokenProvider.expireToken(token)
                handleException(exception, stringJson)
                Result.failure(NetworkError.HttpError.UnauthorizedException)
            } catch (innerException: Exception) {
                handleException(exception, stringJson)
                Result.failure(NetworkError.UnexpectedException(exception))
            }
        }

    private fun handleException(exception: Exception, json: String) {
        parseErrorHandler.handle(
            ParseError.Json(
                content = json,
                exception = exception,
            )
        )
    }

    private fun Frame.text(): String? =
        if (this is Frame.Text) {
            readText()
        } else {
            null
        }

    private fun String.dropBitsFromTheBeginning(): String =
        dropWhile { it.isDigit() }

    private fun String.dropUntilObjectDefinition(): String =
        dropWhile { it != '{' }.dropLastWhile { it != '}' }

    companion object {
        private const val DEFAULT_TIMEOUT = 5000L
    }
}

@Serializable
private class WebSocketRequestInformationResponse(
    val sid: String,
    val upgrades: List<String>,
    val pingInterval: Long,
    val pingTimeout: Long,
)

@Serializable
private class WebSocketErrorResponse(
    val name: String?,
    val message: String?,
    val code: Int?,
)
