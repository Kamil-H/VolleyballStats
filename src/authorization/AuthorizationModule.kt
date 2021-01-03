package com.kamilh.authorization

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

private const val MODULE_NAME = "DI_AUTHORIZATION_MODULE"
val authorizationModule = DI.Module(name = MODULE_NAME) {
    bind<CredentialsValidator>() with provider { StorageBasedCredentialsValidator(instance(), instance()) }
}