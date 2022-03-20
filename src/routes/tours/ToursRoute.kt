package com.kamilh.routes.tours

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di
import routes.respond

fun Routing.tours() {

    val controller by di().instance<ToursController>()

    route("tours") {
        authenticate {
            get {
                call.respond(controller.getTours())
            }
        }
    }
}