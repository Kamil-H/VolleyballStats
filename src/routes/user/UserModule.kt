package com.kamilh.routes.user

import com.kamilh.authorization.HeaderCredentials
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import routes.respond

fun Routing.userRoutes(controller: UserController) {

    route("user") {
        post("register") {
            val deviceId = call.parameters["deviceId"]
            call.respond(controller.addUser(deviceId))
        }

        authenticate {
            get {
                val subscriptionKey = context.principal<HeaderCredentials>()?.subscriptionKey
                call.respond(controller.getUser(subscriptionKey))
            }
        }
    }
}