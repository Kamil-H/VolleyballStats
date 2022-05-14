package com.kamilh

import com.kamilh.authorization.AuthorizationModule
import com.kamilh.interactors.InteractorModule
import com.kamilh.match_analyzer.MatchAnalyzerModule
import com.kamilh.models.*
import com.kamilh.models.api.MappersModule
import com.kamilh.models.api.ResponseMapper
import com.kamilh.models.api.match.MatchResponse
import com.kamilh.models.api.match_report.MatchReportResponse
import com.kamilh.models.api.player_with_details.PlayerWithDetailsResponse
import com.kamilh.models.api.team.TeamResponse
import com.kamilh.models.api.tour.TourResponse
import com.kamilh.repository.RepositoryModule
import com.kamilh.routes.Api
import com.kamilh.routes.RoutesModule
import com.kamilh.routes.TourIdCache
import com.kamilh.routes.TourIdCacheImpl
import com.kamilh.storage.*
import com.kamilh.utils.UtilModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class Singleton

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
}

@Component
abstract class TestComponent(@Component val parent: AppModule) {
    abstract val databaseFactory: DatabaseFactory
    abstract val json: Json
    abstract val leagueStorage: LeagueStorage
    abstract val matchStatisticsStorage: MatchStatisticsStorage
    abstract val matchStorage: MatchStorage
    abstract val playerStorage: PlayerStorage
    abstract val tourStorage: TourStorage
    abstract val teamStorage: TeamStorage

    abstract val matchMapper: ResponseMapper<Match, MatchResponse>
    abstract val matchReportMapper: ResponseMapper<MatchStatistics, MatchReportResponse>
    abstract val playerWithDetailsMapper: ResponseMapper<PlayerWithDetails, PlayerWithDetailsResponse>
    abstract val teamMapper: ResponseMapper<Team, TeamResponse>
    abstract val tourMapper: ResponseMapper<Tour, TourResponse>

    val storages by lazy {
        Storages(
            leagueStorage = leagueStorage,
            matchStatisticsStorage = matchStatisticsStorage,
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
            playerWithDetailsMapper = playerWithDetailsMapper,
            teamMapper = teamMapper,
            tourMapper = tourMapper,
        )
    }

    val api: Api
        @Provides get() = Api(baseUrl = "")

    class Storages(
        val leagueStorage: LeagueStorage,
        val matchStatisticsStorage: MatchStatisticsStorage,
        val matchStorage: MatchStorage,
        val playerStorage: PlayerStorage,
        val tourStorage: TourStorage,
        val teamStorage: TeamStorage,
    )

    class Mappers(
        val matchMapper: ResponseMapper<Match, MatchResponse>,
        val matchReportMapper: ResponseMapper<MatchStatistics, MatchReportResponse>,
        val playerWithDetailsMapper: ResponseMapper<PlayerWithDetails, PlayerWithDetailsResponse>,
        val teamMapper: ResponseMapper<Team, TeamResponse>,
        val tourMapper: ResponseMapper<Tour, TourResponse>,
    )
}