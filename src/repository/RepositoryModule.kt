package com.kamilh.repository

import com.kamilh.repository.models.mappers.MatchResponseToMatchReportMapper
import com.kamilh.repository.parsing.HtmlParser
import com.kamilh.repository.parsing.JsoupHtmlParser
import com.kamilh.repository.parsing.ParseErrorHandler
import com.kamilh.repository.parsing.PrintingParseErrorHandler
import com.kamilh.repository.polishleague.*
import com.kamilh.repository.models.mappers.HtmlToAllMatchesItemMapper
import com.kamilh.repository.models.mappers.HtmlToMatchReportId
import com.kamilh.repository.models.mappers.HtmlToPlayerMapper
import com.kamilh.repository.models.mappers.HtmlToTeamMapper
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.websocket.*
import org.kodein.di.*

private const val MODULE_NAME = "DI_REPOSITORY_MODULE"
val repositoryModule = DI.Module(name = MODULE_NAME) {
    bind<HttpClient>() with provider {
        HttpClient(CIO) {
            install(JsonFeature) {
                serializer = defaultSerializer()
            }
            install(WebSockets)
        }
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
