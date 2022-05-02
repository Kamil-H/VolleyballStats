package com.kamilh.repository

import com.kamilh.Singleton
import com.kamilh.models.*
import com.kamilh.repository.models.mappers.*
import com.kamilh.repository.parsing.HtmlParser
import com.kamilh.repository.parsing.JsoupHtmlParser
import com.kamilh.repository.parsing.ParseErrorHandler
import com.kamilh.repository.parsing.SavableParseErrorHandler
import com.kamilh.repository.polishleague.*
import com.kamilh.utils.cache.Cache
import com.kamilh.utils.cache.ExpirableCache
import com.kamilh.utils.cache.LocalDateTimeCacheValidator
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.websocket.*
import me.tatarka.inject.annotations.Provides
import com.kamilh.models.PlayerWithDetails
import io.ktor.client.HttpClient as Ktor

interface RepositoryModule {

    @Provides
    @Singleton
    fun ktor(): Ktor =
        Ktor(CIO) {
            install(JsonFeature) {
                serializer = defaultSerializer()
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
