package com.kamilh

import com.kamilh.authorization.AccessTokenValidator
import com.kamilh.authorization.headers
import com.kamilh.models.config
import com.kamilh.routes.matches.MatchesController
import com.kamilh.routes.matches.matches
import com.kamilh.routes.players.PlayersController
import com.kamilh.routes.players.players
import com.kamilh.routes.teams.TeamsController
import com.kamilh.routes.teams.teams
import com.kamilh.routes.tours.ToursController
import com.kamilh.routes.tours.tours
import com.kamilh.storage.DatabaseFactory
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@JvmOverloads
fun Application.module(
    appModule: AppModule = AppModule::class.create(scope = this, appConfig = this.config()),
) {
    appModule.applicationInitializer.init(application = this)
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
) {

    fun init(application: Application) = with(application) {
        databaseFactory.connect()

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

        environment.monitor.subscribe(ApplicationStopped) {
            databaseFactory.close()
        }
    }
}