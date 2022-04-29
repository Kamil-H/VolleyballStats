package com.kamilh.routes

import com.kamilh.AppModule
import com.kamilh.authorization.CredentialsValidator
import com.kamilh.routes.user.UserController
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

interface RoutesModule {

    val userController: UserController
        @Provides get

    val credentialsValidator: CredentialsValidator
        @Provides get
}

@Component
abstract class AppRoutesModule(appModule: AppModule) : RoutesModule {

    override val userController: UserController = appModule.userController

    override val credentialsValidator: CredentialsValidator = appModule.credentialsValidator
}