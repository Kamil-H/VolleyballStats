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
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
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
) : MatchReportEndpoint {

    private val url = Url.create("wss://widgets.volleystation.com/api/socket.io/?EIO=3&transport=websocket")

    override suspend fun getMatchReport(matchReportId: MatchReportId, tour: Season): NetworkResult<MatchResponse> {
        val message = "420[\"find\",\"widget/play-by-play\",{\"matchId\":\"${matchReportId.value}\",\"\$limit\":1}]"

        var result: NetworkResult<MatchResponse>? = null
        httpClient.ws(urlString = url.value) {
            val frame = incoming.receive()

            val timeout: Long = if (frame is Frame.Text) {
                val requestInformationResponseText = frame.text()?.dropBitsFromTheBeginning() ?: ""
                getTimeout(requestInformationResponseText)
            } else {
                DEFAULT_TIMEOUT
            }

            delay(timeout)
            send(message)

            var text: String?
            do {
                text = incoming.receive().text()?.dropBitsFromTheBeginning()
            } while (text.isNullOrEmpty())

            val stringJson = text.dropUntilObjectDefinition()
            result = createResult(stringJson)
        }
        return result!!
    }

    private fun getTimeout(requestInformationResponseText: String): Long =
        try {
            val requestInformationResponse = json.decodeFromString<WebSocketRequestInformationResponse>(requestInformationResponseText)
            requestInformationResponse.pingTimeout
        } catch (exception: Exception) {
            parseErrorHandler.handle(
                ParseError.Json(
                    content = requestInformationResponseText,
                    exception = exception,
                )
            )
            DEFAULT_TIMEOUT
        }

    private fun createResult(stringJson: String): NetworkResult<MatchResponse>? =
        try {
            val playByPlayResponse = json.decodeFromString<PlayByPlayResponse>(stringJson)
            if (playByPlayResponse.data.isEmpty()) {
                error("PlayByPlayResponse.data is empty")
            }
            val matchResponse = playByPlayResponse.data.first()
            Result.success(matchResponse)
        } catch (exception: Exception) {
            parseErrorHandler.handle(
                ParseError.Json(
                    content = stringJson,
                    exception = exception,
                )
            )
            Result.failure(NetworkError.UnexpectedException(exception))
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
