package com.kamilh.routes.teams

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di
import routes.respond

fun Routing.teams() {

    val controller by di().instance<TeamsControllerImpl>()

    route("teams") {
        authenticate {
            get {
                val tourId = call.parameters["tourId"]
                call.respond(controller.getTeams(tourId))
            }
        }
    }
}