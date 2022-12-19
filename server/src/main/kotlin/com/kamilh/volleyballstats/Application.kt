package com.kamilh.volleyballstats

import com.kamilh.volleyballstats.authorization.AccessTokenValidator
import com.kamilh.volleyballstats.authorization.headers
import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.utils.Logger
import com.kamilh.volleyballstats.domain.utils.PlatformLogger
import com.kamilh.volleyballstats.interactors.*
import com.kamilh.volleyballstats.models.config
import com.kamilh.volleyballstats.routes.matches.MatchesController
import com.kamilh.volleyballstats.routes.matches.matches
import com.kamilh.volleyballstats.routes.players.PlayersController
import com.kamilh.volleyballstats.routes.players.players
import com.kamilh.volleyballstats.routes.teams.TeamsController
import com.kamilh.volleyballstats.routes.teams.teams
import com.kamilh.volleyballstats.routes.tours.ToursController
import com.kamilh.volleyballstats.routes.tours.tours
import com.kamilh.volleyballstats.storage.DatabaseFactory
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import org.slf4j.event.Level

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

@JvmOverloads
fun Application.module(
    inTest: Boolean = false,
    appModule: AppModule = AppModule::class.create(scope = this, appConfig = this.config()),
) {
    appModule.applicationInitializer.init(application = this, inTest = inTest)
}

@Inject
class ApplicationInitializer(
    private val databaseFactory: DatabaseFactory,
    private val json: Json,
    private val accessTokenValidator: AccessTokenValidator,
    private val matchesController: MatchesController,
    private val playersController: PlayersController,
    private val teamsController: TeamsController,
    private val toursController: ToursController,
    private val synchronizer: Synchronizer,
    private val platformLogger: PlatformLogger,
    private val delayedSynchronizeScheduler: DelayedSynchronizeScheduler,
    private val synchronizeStateHolder: SynchronizeStateHolder,
    private val cacheInvalidator: CacheInvalidator,
) {

    fun init(application: Application, inTest: Boolean) = with(application) {
        configureLogging()
        configureDatabase()
        configureSynchronizer(inTest)

        install(Authentication) {
            headers(accessTokenValidator = accessTokenValidator)
        }

        install(ContentNegotiation) {
            json(
                contentType = ContentType.Application.Json,
                json = json
            )
        }

        install(StatusPages) {
            exception<Throwable> { call, cause ->
                cause.printStackTrace()
                Logger.e(message = cause.stackTraceToString())
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        install(Routing) {
            matches(matchesController)
            players(playersController)
            teams(teamsController)
            tours(toursController)
        }
    }

    private fun Application.configureLogging() {
        Logger.setLogger(platformLogger)

        install(CallLogging) {
            level = Level.INFO
            format { call ->
                val status = call.response.status()?.value
                val httpMethod = call.request.httpMethod.value
                val remoteHost = call.request.local.remoteHost
                val userAgent = call.request.headers["User-Agent"]
                val path = call.request.path()
                val queryString = call.request.queryString()
                "$status [$httpMethod] $path$queryString | HOST: $remoteHost, $userAgent"
            }
        }
    }

    private fun Application.configureDatabase() {
        databaseFactory.connect()
        environment.monitor.subscribe(ApplicationStopped) {
            databaseFactory.close()
        }
    }

    private fun Application.configureSynchronizer(inTest: Boolean) {
        if (!inTest) {
            val league = League.POLISH_LEAGUE
            synchronizer.synchronize(league)

            delayedSynchronizeScheduler.synchronizeSignal
                .onEach { synchronizer.synchronize(it.league) }
                .launchIn(this)

            synchronizeStateHolder.receive()
                .filterIsInstance<SynchronizeState.Success>()
                .onEach { cacheInvalidator() }
                .launchIn(this)
        }
    }
}
