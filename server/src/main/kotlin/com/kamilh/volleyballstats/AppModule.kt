package com.kamilh.volleyballstats

import com.kamilh.volleyballstats.api.MappersModule
import com.kamilh.volleyballstats.authorization.AuthorizationModule
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.interactors.InteractorModule
import com.kamilh.volleyballstats.matchanalyzer.MatchAnalyzerModule
import com.kamilh.volleyballstats.models.AppConfig
import com.kamilh.volleyballstats.repository.RepositoryModule
import com.kamilh.volleyballstats.routes.RoutesModule
import com.kamilh.volleyballstats.routes.TourIdCache
import com.kamilh.volleyballstats.routes.TourIdCacheImpl
import com.kamilh.volleyballstats.storage.SqlDriverFactory
import com.kamilh.volleyballstats.storage.StorageModule
import com.kamilh.volleyballstats.utils.UtilModule
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@Singleton
abstract class AppModule(
    private val scope: CoroutineScope,
    private val appConfig: AppConfig,
) : UtilModule, RepositoryModule, StorageModule, InteractorModule, MatchAnalyzerModule, AuthorizationModule,
    MappersModule, RoutesModule {

    abstract val applicationInitializer: ApplicationInitializer
    abstract val initializer: TestApplicationInitializer

    val TourIdCacheImpl.bind: TourIdCache
        @Provides get() = this

    @Singleton
    @Provides
    fun json(): Json =
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

    @Provides
    fun coroutineScope(): CoroutineScope = scope

    @Provides
    fun appConfig(): AppConfig = appConfig

    @Singleton
    @Provides
    fun appDispatchers(): AppDispatchers =
        AppDispatchers(
            io = Dispatchers.IO,
            main = Dispatchers.Main,
            default = Dispatchers.Default,
        )

    @Provides
    fun sqlDriverCreator(): SqlDriverFactory = object : SqlDriverFactory {
        override fun create(): SqlDriver = JdbcSqliteDriver(url = appConfig.databaseConfig.jdbcUrl)
    }
}

