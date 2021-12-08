package com.kamilh

import com.kamilh.authorization.CredentialsValidator
import com.kamilh.authorization.headers
import com.kamilh.routes.user.userRoutes
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.instance
import org.kodein.di.ktor.di
import storage.DatabaseFactory

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(appModule: DI.Module = applicationModule(this)) {
    di { import(appModule) }

    val databaseFactory by di().instance<DatabaseFactory>()
    databaseFactory.connect()

    install(Authentication) {
        val credentialsValidator by di().instance<CredentialsValidator>()
        headers(
            credentialsValidator = credentialsValidator
        )
    }

    install(ContentNegotiation) {
        val json by di().instance<Json>()
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
        userRoutes()
    }

    environment.monitor.subscribe(ApplicationStopped) {
        databaseFactory.close()
    }
}