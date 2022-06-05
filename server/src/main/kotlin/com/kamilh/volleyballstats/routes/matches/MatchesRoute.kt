package com.kamilh.volleyballstats.routes.matches

import com.kamilh.volleyballstats.api.ApiConstants
import com.kamilh.volleyballstats.routes.respond
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Routing.matches(controller: MatchesController) {

    route(ApiConstants.PATH_SEGMENT_MATCHES) {
        authenticate {
            get {
                val tourId = call.parameters[ApiConstants.QUERY_PARAM_TOUR_ID]
                call.respond(controller.getMatches(tourId))
            }
        }
    }

    route("${ApiConstants.PATH_SEGMENT_MATCHES}/${ApiConstants.PATH_SEGMENT_REPORT}/{${ApiConstants.PATH_SEGMENT_ID}}") {
        authenticate {
            get {
                val id = call.parameters[ApiConstants.PATH_SEGMENT_ID]
                call.respond(controller.getMatchReport(id))
            }
        }
    }
}