package com.kamilh

import com.kamilh.authorization.authorizationModule
import com.kamilh.interactors.interactorModule
import com.kamilh.storage.storageModule
import com.kamilh.utils.utilsModule
import org.kodein.di.DI

private const val MODULE_NAME = "DI_APPLICATION_MODULE"
val applicationModule = DI.Module(name = MODULE_NAME) {
    import(utilsModule)
    import(storageModule)
    import(interactorModule)
    import(authorizationModule)
}