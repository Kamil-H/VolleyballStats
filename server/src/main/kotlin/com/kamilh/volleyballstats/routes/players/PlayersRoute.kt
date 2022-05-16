package com.kamilh.volleyballstats.routes.players

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import com.kamilh.volleyballstats.routes.respond

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