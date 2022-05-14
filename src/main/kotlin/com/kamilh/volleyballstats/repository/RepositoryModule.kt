package com.kamilh.volleyballstats.repository

import com.kamilh.volleyballstats.Singleton
import com.kamilh.volleyballstats.models.*
import com.kamilh.volleyballstats.repository.models.mappers.*
import com.kamilh.volleyballstats.repository.parsing.HtmlParser
import com.kamilh.volleyballstats.repository.parsing.JsoupHtmlParser
import com.kamilh.volleyballstats.repository.parsing.ParseErrorHandler
import com.kamilh.volleyballstats.repository.parsing.SavableParseErrorHandler
import com.kamilh.volleyballstats.repository.polishleague.*
import com.kamilh.volleyballstats.utils.cache.Cache
import com.kamilh.volleyballstats.utils.cache.ExpirableCache
import com.kamilh.volleyballstats.utils.cache.LocalDateTimeCacheValidator
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
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
                level = LogLevel.HEADERS
                logger = object : Logger {
                    override fun log(message: String) {
                        com.kamilh.volleyballstats.utils.Logger.d(message = message)
                    }
                }
            }
            install(WebSockets)
        }

    @Provides
    @Singleton
    fun allPlayersCache(): Cache<Unit, List<Player>> =
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

    val HttpPolishLeagueRepository.bind: PolishLeagueRepository
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

    val HtmlToPlayerMapper.bind: HtmlMapper<List<Player>>
        @Provides get() = this

    val HtmlToAllMatchesItemMapper.bind: HtmlMapper<List<MatchInfo>>
        @Provides get() = this

    val HtmlToMatchReportId.bind: HtmlMapper<MatchReportId>
        @Provides get() = this

    val HtmlToPlayerDetailsMapper.bind: HtmlMapper<PlayerDetails>
        @Provides get() = this

    val HtmlToPlayerWithDetailsMapper.bind: HtmlMapper<PlayerWithDetails>
        @Provides get() = this
}
