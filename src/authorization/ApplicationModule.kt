package com.kamilh.authorization

import com.kamilh.storage.storageModule
import org.kodein.di.DI

private const val MODULE_NAME = "DI_APPLICATION_MODULE"
val applicationModule = DI.Module(name = MODULE_NAME) {
    import(storageModule)
    import(authorizationModule)
}