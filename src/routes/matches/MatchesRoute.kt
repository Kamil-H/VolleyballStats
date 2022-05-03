package com.kamilh.routes.matches

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import routes.respond

fun Routing.matches(controller: MatchesController) {

    route("matches") {
        authenticate {
            get {
                val tourId = call.parameters["tourId"]
                call.respond(controller.getMatches(tourId))
            }
        }
    }

    route("matches/report/{id}") {
        authenticate {
            get {
                val id = call.parameters["id"]
                call.respond(controller.getMatchReport(id))
            }
        }
    }
}