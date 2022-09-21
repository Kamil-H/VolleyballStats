package com.kamilh.volleyballstats.presentation.di

import com.kamilh.volleyballstats.api.ApiConstants
import com.kamilh.volleyballstats.api.ApiUrl
import com.kamilh.volleyballstats.clients.data.DataModule
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildInfo
import com.kamilh.volleyballstats.domain.models.buildinfo.BuildType
import com.kamilh.volleyballstats.domain.utils.*
import com.kamilh.volleyballstats.domain.utils.Logger
import com.kamilh.volleyballstats.interactors.Synchronizer
import com.kamilh.volleyballstats.network.HttpClient
import com.kamilh.volleyballstats.network.KtorHttpClient
import com.kamilh.volleyballstats.presentation.interactors.InteractorModule
import com.kamilh.volleyballstats.presentation.utils.AccessTokenProvider
import com.kamilh.volleyballstats.presentation.utils.AppInitializer
import com.kamilh.volleyballstats.presentation.utils.ClientLogger
import com.kamilh.volleyballstats.storage.SqlDriverFactory
import com.kamilh.volleyballstats.storage.stats.AttackStats
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import io.ktor.client.HttpClient as Ktor

@Component
interface PresentationModule : InteractorModule, DataModule {

    val synchronizer: Synchronizer

    val appInitializer: AppInitializer

    val attackStats: AttackStats

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
        }

    val KtorHttpClient.bind: HttpClient
        @Provides get() = this

    val ConsoleExceptionLogger.bind: ExceptionLogger
        @Provides get() = this

    val ClientLogger.bind: PlatformLogger
        @Provides get() = this

    companion object {
        private const val DATABASE_NAME = "volleyballstats.db"
    }
}
