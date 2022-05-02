package com.kamilh.routes.players

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import routes.respond

fun Routing.players(controller: PlayersController) {

    route("players") {
        authenticate {
            get {
                val tourId = call.parameters["tourId"]
                call.respond(controller.getPlayersWithDetails(tourId))
            }
        }
    }
}