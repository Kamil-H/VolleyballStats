package com.kamilh

import com.kamilh.authorization.SimpleValidator
import com.kamilh.authorization.headers
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Authentication) {
        headers(
            credentialsValidator = SimpleValidator()
        )
    }

    install(ContentNegotiation) {
        json(
            contentType = ContentType.Application.Json,
            json = Json {
                prettyPrint = true
            }
        )
    }

    routing {
        authenticate {
            get("/") {
                call.respond(User(12313, "Kamil"))
            }
        }

        install(StatusPages) {
            exception<Throwable> { cause ->
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}

@Serializable
data class User(val id: Long, val name: String)
