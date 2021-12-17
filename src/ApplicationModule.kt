package com.kamilh

import com.kamilh.authorization.authorizationModule
import com.kamilh.interactors.interactorModule
import com.kamilh.match_analyzer.matchAnalyzerModule
import com.kamilh.models.AppConfig
import com.kamilh.models.config
import com.kamilh.repository.repositoryModule
import com.kamilh.routes.routesModule
import com.kamilh.storage.storageModule
import com.kamilh.utils.utilsModule
import io.ktor.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import org.kodein.di.*

private const val MODULE_NAME = "DI_APPLICATION_MODULE"
fun applicationModule(scope: CoroutineScope, appConfig: AppConfig? = null) = DI.Module(name = MODULE_NAME) {
    bind<Json>() with provider {
        Json {
            prettyPrint = true
        }
    }
    bind<CoroutineScope>() with provider { scope }

    bindProvider {
        if (appConfig == null) {
            val app by di.instance<Application>()
            app.config()
        } else {
            appConfig
        }
    }

    import(utilsModule)
    import(routesModule)
    import(storageModule)
    import(repositoryModule)
    import(interactorModule)
    import(authorizationModule)
    import(matchAnalyzerModule)
}