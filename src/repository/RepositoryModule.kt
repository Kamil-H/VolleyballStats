package com.kamilh.repository

import com.kamilh.repository.models.mappers.*
import com.kamilh.repository.parsing.HtmlParser
import com.kamilh.repository.parsing.JsoupHtmlParser
import com.kamilh.repository.parsing.ParseErrorHandler
import com.kamilh.repository.parsing.PrintingParseErrorHandler
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
    bindProvider { HtmlToTeamMapper(instance()) }
    bindProvider { HtmlToPlayerMapper(instance()) }
    bindProvider { HtmlToMatchReportId() }
    bindProvider { HtmlToAllMatchesItemMapper(instance()) }
    bindProvider { MatchResponseToMatchReportMapper() }

    bind<HtmlParser>() with provider {
        JsoupHtmlParser()
    }

    bind<FileManager>() with provider {
        IoFileManager(instance(), instance())
    }

    bind<ParseErrorHandler>() with provider {
        PrintingParseErrorHandler(instance(), instance())
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
