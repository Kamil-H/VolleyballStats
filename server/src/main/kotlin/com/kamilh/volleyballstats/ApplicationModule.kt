package com.kamilh.volleyballstats

import com.kamilh.volleyballstats.api.Api
import com.kamilh.volleyballstats.api.MappersModule
import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.api.match.MatchResponse
import com.kamilh.volleyballstats.api.match_report.MatchReportResponse
import com.kamilh.volleyballstats.api.player.PlayerResponse
import com.kamilh.volleyballstats.api.team.TeamResponse
import com.kamilh.volleyballstats.api.tour.TourResponse
import com.kamilh.volleyballstats.authorization.AuthorizationModule
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.interactors.InteractorModule
import com.kamilh.volleyballstats.match_analyzer.MatchAnalyzerModule
import com.kamilh.volleyballstats.models.AppConfig
import com.kamilh.volleyballstats.repository.RepositoryModule
import com.kamilh.volleyballstats.routes.RoutesModule
import com.kamilh.volleyballstats.routes.TourIdCache
import com.kamilh.volleyballstats.routes.TourIdCacheImpl
import com.kamilh.volleyballstats.storage.*
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

@Component
abstract class TestComponent(@Component val parent: AppModule) {
    abstract val databaseFactory: DatabaseFactory
    abstract val json: Json
    abstract val leagueStorage: LeagueStorage
    abstract val matchReportStorage: MatchReportStorage
    abstract val matchStorage: MatchStorage
    abstract val playerStorage: PlayerStorage
    abstract val tourStorage: TourStorage
    abstract val teamStorage: TeamStorage

    abstract val matchMapper: ResponseMapper<Match, MatchResponse>
    abstract val matchReportMapper: ResponseMapper<MatchReport, MatchReportResponse>
    abstract val playerMapper: ResponseMapper<Player, PlayerResponse>
    abstract val teamMapper: ResponseMapper<Team, TeamResponse>
    abstract val tourMapper: ResponseMapper<Tour, TourResponse>

    val storages by lazy {
        Storages(
            leagueStorage = leagueStorage,
            matchReportStorage = matchReportStorage,
            matchStorage = matchStorage,
            playerStorage = playerStorage,
            tourStorage = tourStorage,
            teamStorage = teamStorage,
        )
    }

    val mappers by lazy {
        Mappers(
            matchMapper = matchMapper,
            matchReportMapper = matchReportMapper,
            playerMapper = playerMapper,
            teamMapper = teamMapper,
            tourMapper = tourMapper,
        )
    }

    val api: Api
        @Provides get() = Api(baseUrl = "")

    class Storages(
        val leagueStorage: LeagueStorage,
        val matchReportStorage: MatchReportStorage,
        val matchStorage: MatchStorage,
        val playerStorage: PlayerStorage,
        val tourStorage: TourStorage,
        val teamStorage: TeamStorage,
    )

    class Mappers(
        val matchMapper: ResponseMapper<Match, MatchResponse>,
        val matchReportMapper: ResponseMapper<MatchReport, MatchReportResponse>,
        val playerMapper: ResponseMapper<Player, PlayerResponse>,
        val teamMapper: ResponseMapper<Team, TeamResponse>,
        val tourMapper: ResponseMapper<Tour, TourResponse>,
    )
}