package com.kamilh.volleyballstats.repository

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.MatchInfo
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Team
import com.kamilh.volleyballstats.models.MatchReportId
import com.kamilh.volleyballstats.models.PlayerDetails
import com.kamilh.volleyballstats.models.PlayerSnapshot
import com.kamilh.volleyballstats.models.PlayerWithDetails
import com.kamilh.volleyballstats.models.TeamPlayer
import com.kamilh.volleyballstats.network.HttpClient
import com.kamilh.volleyballstats.network.KtorHttpClient
import com.kamilh.volleyballstats.network.repository.PolishLeagueRepository
import com.kamilh.volleyballstats.repository.models.mappers.HtmlMapper
import com.kamilh.volleyballstats.repository.models.mappers.HtmlToAllMatchesItemMapper
import com.kamilh.volleyballstats.repository.models.mappers.HtmlToMatchReportId
import com.kamilh.volleyballstats.repository.models.mappers.HtmlToPlayerDetailsMapper
import com.kamilh.volleyballstats.repository.models.mappers.HtmlToPlayerMapper
import com.kamilh.volleyballstats.repository.models.mappers.HtmlToPlayerWithDetailsMapper
import com.kamilh.volleyballstats.repository.models.mappers.HtmlToTeamMapper
import com.kamilh.volleyballstats.repository.models.mappers.HtmlToTeamPlayerMapper
import com.kamilh.volleyballstats.repository.parsing.HtmlParser
import com.kamilh.volleyballstats.repository.parsing.JsoupHtmlParser
import com.kamilh.volleyballstats.repository.parsing.ParseErrorHandler
import com.kamilh.volleyballstats.repository.parsing.SavableParseErrorHandler
import com.kamilh.volleyballstats.repository.polishleague.FileBasedMatchResponseStorage
import com.kamilh.volleyballstats.repository.polishleague.HttpPlsRepository
import com.kamilh.volleyballstats.repository.polishleague.InMemoryTourCache
import com.kamilh.volleyballstats.repository.polishleague.MatchReportEndpoint
import com.kamilh.volleyballstats.repository.polishleague.MatchResponseStorage
import com.kamilh.volleyballstats.repository.polishleague.PlsRepository
import com.kamilh.volleyballstats.repository.polishleague.TourCache
import com.kamilh.volleyballstats.repository.polishleague.WebSocketMatchReportEndpoint
import com.kamilh.volleyballstats.repository.sockettoken.FileSocketTokenProvider
import com.kamilh.volleyballstats.repository.sockettoken.SocketTokenProvider
import com.kamilh.volleyballstats.utils.cache.Cache
import com.kamilh.volleyballstats.utils.cache.ExpirableCache
import com.kamilh.volleyballstats.utils.cache.LocalDateTimeCacheValidator
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides
import io.ktor.client.HttpClient as Ktor

interface RepositoryModule {

    @Provides
    @Singleton
    fun ktor(json: Json): Ktor =
        Ktor(CIO) {
            install(ContentNegotiation) {
                json(json = json)
            }
            install(Logging) {
                level = LogLevel.INFO
                logger = object : Logger {
                    override fun log(message: String) {
                        com.kamilh.volleyballstats.domain.utils.Logger.d(message = message)
                    }
                }
            }
            install(WebSockets)
        }

    @Provides
    @Singleton
    fun allPlayersCache(): Cache<Unit, List<PlayerSnapshot>> =
        ExpirableCache(LocalDateTimeCacheValidator())

    @Provides
    @Singleton
    fun allPlayersByTourCache(): Cache<Season, List<TeamPlayer>> =
        ExpirableCache(LocalDateTimeCacheValidator())

    val KtorHttpClient.bind: HttpClient
        @Provides get() = this

    val IoFileManager.bind: FileManager
        @Provides get() = this

    val WebSocketMatchReportEndpoint.bind: MatchReportEndpoint
        @Provides get() = this

    val FileBasedMatchResponseStorage.bind: MatchResponseStorage
        @Provides get() = this

    val HttpPlsRepository.bindPlsRepository: PlsRepository
        @Provides get() = this

    val HttpPlsRepository.bindPolishLeagueRepository: PolishLeagueRepository
        @Provides get() = this

    val InMemoryTourCache.bind: TourCache
        @Provides get() = this

    val JsoupHtmlParser.bind: HtmlParser
        @Provides get() = this

    val SavableParseErrorHandler.bind: ParseErrorHandler
        @Provides get() = this

    val HtmlToTeamMapper.bind: HtmlMapper<List<Team>>
        @Provides get() = this

    val HtmlToTeamPlayerMapper.bind: HtmlMapper<List<TeamPlayer>>
        @Provides get() = this

    val HtmlToPlayerMapper.bind: HtmlMapper<List<PlayerSnapshot>>
        @Provides get() = this

    val HtmlToAllMatchesItemMapper.bind: HtmlMapper<List<MatchInfo>>
        @Provides get() = this

    val HtmlToMatchReportId.bind: HtmlMapper<MatchReportId>
        @Provides get() = this

    val HtmlToPlayerDetailsMapper.bind: HtmlMapper<PlayerDetails>
        @Provides get() = this

    val HtmlToPlayerWithDetailsMapper.bind: HtmlMapper<PlayerWithDetails>
        @Provides get() = this

    val FileSocketTokenProvider.bind: SocketTokenProvider
        @Provides get() = this
}
