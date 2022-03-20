package com.kamilh.routes.matches

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di
import routes.respond

fun Routing.matches() {

    val controller by di().instance<MatchesController>()

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