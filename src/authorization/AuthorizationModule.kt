package com.kamilh.authorization

import io.ktor.server.auth.*
import me.tatarka.inject.annotations.Provides

interface AuthorizationModule {

    val HeadersAuthorization.bind: AuthenticationProvider
        @Provides get() = this
}