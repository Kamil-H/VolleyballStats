package com.kamilh.utils

import com.kamilh.models.AppDispatchers
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.provider

private const val MODULE_NAME = "DI_UTILS_MODULE"
val utilsModule = DI.Module(name = MODULE_NAME) {
    bind<AppDispatchers>() with provider {
        AppDispatchers(
            io = Dispatchers.IO,
            main = Dispatchers.Main,
            default = Dispatchers.Default,
        )
    }
    bind<UuidCreator>() with provider {
        JavaUtilUuidCreator()
    }
    bind<UuidValidator>() with provider {
        JavaUtilUuidValidator()
    }
}