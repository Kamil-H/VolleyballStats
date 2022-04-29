package com.kamilh.routes.user

import com.kamilh.authorization.HeaderCredentials
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
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