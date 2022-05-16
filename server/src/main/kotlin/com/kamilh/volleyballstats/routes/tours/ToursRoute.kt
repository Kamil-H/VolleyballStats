package com.kamilh.volleyballstats.routes.tours

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import com.kamilh.volleyballstats.routes.respond

fun Routing.tours(controller: ToursController) {

    route("tours") {
        authenticate {
            get {
                call.respond(controller.getTours())
            }
        }
    }
}