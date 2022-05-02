package com.kamilh.routes.matches

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
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