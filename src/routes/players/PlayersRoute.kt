package com.kamilh.routes.players

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di
import routes.respond

fun Routing.players() {

    val controller by di().instance<PlayersController>()

    route("players") {
        authenticate {
            get {
                val tourId = call.parameters["tourId"]
                call.respond(controller.getPlayersWithDetails(tourId))
            }
        }
    }
}