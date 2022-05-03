package com.kamilh.routes.tours

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import routes.respond

fun Routing.tours(controller: ToursController) {

    route("tours") {
        authenticate {
            get {
                call.respond(controller.getTours())
            }
        }
    }
}