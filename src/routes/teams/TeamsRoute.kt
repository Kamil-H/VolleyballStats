package com.kamilh.routes.teams

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import routes.respond

fun Routing.teams(controller: TeamsController) {

    route("teams") {
        authenticate {
            get {
                val tourId = call.parameters["tourId"]
                call.respond(controller.getTeams(tourId))
            }
        }
    }
}