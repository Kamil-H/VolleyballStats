package com.kamilh.volleyballstats.routes.tours

import com.kamilh.volleyballstats.api.ApiConstants
import com.kamilh.volleyballstats.routes.respond
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Routing.tours(controller: ToursController) {

    route(ApiConstants.PATH_SEGMENT_TOURS) {
        authenticate {
            get {
                call.respond(controller.getTours())
            }
        }
    }
}
