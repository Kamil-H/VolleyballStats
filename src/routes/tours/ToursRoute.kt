package com.kamilh.routes.tours

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
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