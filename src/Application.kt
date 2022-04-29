package com.kamilh

import com.kamilh.authorization.headers
import com.kamilh.models.AppConfig
import com.kamilh.models.config
import com.kamilh.routes.AppRoutesModule
import com.kamilh.routes.RoutesModule
import com.kamilh.routes.create
import com.kamilh.routes.user.userRoutes
import com.kamilh.storage.DatabaseFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(
    appConfig: AppConfig = this.config(),
    routesModule: RoutesModule? = null,
) {

    val appModule = AppModule::class.create(
        scope = this,
        appConfig = appConfig,
    )
    appModule.applicationInitializer.init(
        application = this,
        routesModule = routesModule ?: AppRoutesModule::class.create(appModule),
    )
}

@Inject
class ApplicationInitializer(
    private val databaseFactory: DatabaseFactory,
    private val json: Json,
) {

    fun init(application: Application, routesModule: RoutesModule) = with(application) {
        databaseFactory.connect()

        install(Authentication) {
            headers(credentialsValidator = routesModule.credentialsValidator)
        }

        install(ContentNegotiation) {
            json(
                contentType = ContentType.Application.Json,
                json = json
            )
        }

        install(StatusPages) {
            exception<Throwable> { cause ->
                cause.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        install(Routing) {
            userRoutes(routesModule.userController)
        }

        environment.monitor.subscribe(ApplicationStopped) {
            databaseFactory.close()
        }
    }
}