package com.kamilh.volleyballstats

import com.kamilh.volleyballstats.authorization.AccessTokenValidator
import com.kamilh.volleyballstats.authorization.headers
import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.interactors.Synchronizer
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
import com.kamilh.volleyballstats.domain.utils.PlatformLogger
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

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
) {

    fun init(application: Application, inTest: Boolean) = with(application) {
        configureLogger()
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

        // TODO: detect 404 and usage of wrong method (eg. POST instead of GET)
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                cause.printStackTrace()
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

    private fun configureLogger() {
        com.kamilh.volleyballstats.domain.utils.Logger.setLogger(platformLogger)
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
            launch { synchronizer.synchronize(league) }
        }
    }
}