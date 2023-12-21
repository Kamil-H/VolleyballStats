package com.kamilh.volleyballstats.presentation.di

import app.cash.sqldelight.db.SqlDriver
import com.kamilh.volleyballstats.api.ApiConstants
import com.kamilh.volleyballstats.api.ApiUrl
import com.kamilh.volleyballstats.clients.data.DataModule
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildInfo
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildType
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.domain.utils.ConsoleExceptionLogger
import com.kamilh.volleyballstats.domain.utils.ExceptionLogger
import com.kamilh.volleyballstats.domain.utils.Logger
import com.kamilh.volleyballstats.domain.utils.PlatformLogger
import com.kamilh.volleyballstats.network.HttpClient
import com.kamilh.volleyballstats.network.KtorHttpClient
import com.kamilh.volleyballstats.presentation.features.PresentersModule
import com.kamilh.volleyballstats.presentation.features.filter.MockStatsFiltersStorage
import com.kamilh.volleyballstats.presentation.features.filter.StatsFiltersStorage
import com.kamilh.volleyballstats.presentation.features.stats.StatsFlowFactory
import com.kamilh.volleyballstats.presentation.features.stats.StatsFlowFactoryImpl
import com.kamilh.volleyballstats.presentation.features.stats.StatsModelMapper
import com.kamilh.volleyballstats.presentation.features.stats.StatsModelMapperImpl
import com.kamilh.volleyballstats.presentation.interactors.InteractorModule
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventHandler
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventReceiver
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventSender
import com.kamilh.volleyballstats.presentation.utils.AccessTokenProvider
import com.kamilh.volleyballstats.presentation.utils.AppInitializer
import com.kamilh.volleyballstats.presentation.utils.ClientLogger
import com.kamilh.volleyballstats.storage.SqlDriverFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides
import io.ktor.client.HttpClient as Ktor

interface PresentationModule : InteractorModule, DataModule, PresentersModule {

    val appInitializer: AppInitializer

    @Provides
    @Singleton
    fun coroutineScope(appDispatchers: AppDispatchers): CoroutineScope =
        CoroutineScope(context = appDispatchers.main + SupervisorJob())

    @Singleton
    @Provides
    fun appDispatchers(): AppDispatchers =
        AppDispatchers(
            io = Dispatchers.Default,
            main = Dispatchers.Main.immediate,
            default = Dispatchers.Default,
        )

    @Provides
    @Singleton
    fun ktor(accessTokenProvider: AccessTokenProvider): Ktor =
        HttpClient {
            install(ContentNegotiation) {
                val json = Json {
                    prettyPrint = true
                }
                json(json = json)
            }
            install(Logging) {
                level = LogLevel.HEADERS
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        Logger.d(message = message)
                    }
                }
            }
            defaultRequest {
                headers.append(ApiConstants.HEADER_ACCESS_TOKEN, accessTokenProvider.get().value)
            }
        }

    @Provides
    fun sqlDriverFactory(dependencyFactory: DependencyFactory): SqlDriverFactory = object : SqlDriverFactory {
        override fun create(): SqlDriver = dependencyFactory.createSqlDriver(databaseName = DATABASE_NAME)
    }

    @Provides
    fun apiUrl(buildInfo: BuildInfo): ApiUrl =
        when (buildInfo.buildType) {
            BuildType.Debug -> ApiUrl.DEBUG
            BuildType.Release -> ApiUrl.RELEASE
            BuildType.Local -> ApiUrl.LOCAL
        }

    val KtorHttpClient.bind: HttpClient
        @Provides get() = this

    val ConsoleExceptionLogger.bind: ExceptionLogger
        @Provides get() = this

    val ClientLogger.bind: PlatformLogger
        @Provides get() = this

    // TODO: Move it to storage
    val MockStatsFiltersStorage.bind: StatsFiltersStorage
        @Provides get() = this

    val StatsModelMapperImpl.bind: StatsModelMapper
        @Provides get() = this

    val StatsFlowFactoryImpl.bind: StatsFlowFactory
        @Provides get() = this

    val NavigationEventHandler.bindSender: NavigationEventSender
        @Provides get() = this

    val NavigationEventHandler.bindReceiver: NavigationEventReceiver
        @Provides get() = this

    companion object {
        private const val DATABASE_NAME = "volleyballstats.db"
    }
}
