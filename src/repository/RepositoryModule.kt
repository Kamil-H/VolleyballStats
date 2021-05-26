package com.kamilh.repository

import com.kamilh.models.AllMatchesItem
import com.kamilh.models.MatchReportId
import com.kamilh.models.Player
import com.kamilh.models.Team
import com.kamilh.repository.models.mappers.*
import com.kamilh.repository.parsing.HtmlParser
import com.kamilh.repository.parsing.JsoupHtmlParser
import com.kamilh.repository.parsing.ParseErrorHandler
import com.kamilh.repository.parsing.SavableParseErrorHandler
import com.kamilh.repository.polishleague.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.websocket.*
import org.kodein.di.*
import io.ktor.client.HttpClient as Ktor

private const val MODULE_NAME = "DI_REPOSITORY_MODULE"
val repositoryModule = DI.Module(name = MODULE_NAME) {
    bind<Ktor>() with provider {
        Ktor(CIO) {
            install(JsonFeature) {
                serializer = defaultSerializer()
            }
            install(WebSockets)
        }
    }

    bind<HttpClient>() with provider {
        KtorHttpClient(instance(), instance())
    }

    bindProvider { PolishLeagueApi() }
    bind<HtmlMapper<List<Team>>>() with provider { HtmlToTeamMapper(instance()) }
    bind<HtmlMapper<List<Player>>>() with provider { HtmlToPlayerMapper(instance()) }
    bind<HtmlMapper<MatchReportId>>() with provider { HtmlToMatchReportId() }
    bind<HtmlMapper<List<AllMatchesItem>>>() with provider { HtmlToAllMatchesItemMapper(instance()) }
    bindProvider { MatchResponseToMatchReportMapper() }

    bind<HtmlParser>() with provider {
        JsoupHtmlParser()
    }

    bind<FileManager>() with provider {
        IoFileManager(instance(), instance())
    }

    bind<ParseErrorHandler>() with provider {
        SavableParseErrorHandler(instance(), instance())
    }

    bind<MatchReportEndpoint>() with provider {
        WebSocketMatchReportEndpoint(instance(), instance(), instance())
    }

    bind<PolishLeagueRepository>() with provider {
        HttpPolishLeagueRepository(instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance(), instance())
    }

    bind<MatchResponseStorage>() with provider {
        FileBasedMatchResponseStorage(instance(), instance())
    }
}
