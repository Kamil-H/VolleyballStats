package com.kamilh.volleyballstats.routes.players

import com.kamilh.volleyballstats.api.ApiConstants
import com.kamilh.volleyballstats.routes.respond
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Routing.players(controller: PlayersController) {

    route(ApiConstants.PATH_SEGMENT_PLAYERS) {
        authenticate {
            get {
                val tourId = call.parameters[ApiConstants.QUERY_PARAM_TOUR_ID]
                call.respond(controller.getPlayersWithDetails(tourId))
            }
        }
    }
}
