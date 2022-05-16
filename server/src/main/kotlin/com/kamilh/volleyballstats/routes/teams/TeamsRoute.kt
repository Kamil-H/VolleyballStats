package com.kamilh.volleyballstats.routes.teams

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import com.kamilh.volleyballstats.routes.respond

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