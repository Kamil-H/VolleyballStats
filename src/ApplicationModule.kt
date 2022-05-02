package com.kamilh

import com.kamilh.authorization.AuthorizationModule
import com.kamilh.authorization.StorageBasedCredentialsValidator
import com.kamilh.interactors.InteractorModule
import com.kamilh.match_analyzer.MatchAnalyzerModule
import com.kamilh.models.AppConfig
import com.kamilh.models.AppDispatchers
import com.kamilh.models.api.MappersModule
import com.kamilh.repository.RepositoryModule
import com.kamilh.routes.TourIdCache
import com.kamilh.routes.TourIdCacheImpl
import com.kamilh.routes.matches.MatchesControllerImpl
import com.kamilh.routes.players.PlayersControllerImpl
import com.kamilh.routes.teams.TeamsControllerImpl
import com.kamilh.routes.tours.ToursControllerImpl
import com.kamilh.routes.user.UserControllerImpl
import com.kamilh.storage.StorageModule
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
) : UtilModule, RepositoryModule, StorageModule, InteractorModule, MatchAnalyzerModule, AuthorizationModule, MappersModule {

    abstract val applicationInitializer: ApplicationInitializer
    abstract val initializer: TestApplicationInitializer
    abstract val userController: UserControllerImpl
    abstract val credentialsValidator: StorageBasedCredentialsValidator
    abstract val matchesController: MatchesControllerImpl
    abstract val playersController: PlayersControllerImpl
    abstract val teamsController: TeamsControllerImpl
    abstract val toursController: ToursControllerImpl

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
