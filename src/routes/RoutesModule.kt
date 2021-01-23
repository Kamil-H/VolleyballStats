package com.kamilh.routes

import com.kamilh.routes.user.UserController
import com.kamilh.routes.user.UserControllerImpl
import org.kodein.di.*

private const val MODULE_NAME = "DI_ROUTES_MODULE"
val routesModule = DI.Module(name = MODULE_NAME) {
    bind<UserController>() with provider {
        UserControllerImpl(instance(), instance())
    }
}