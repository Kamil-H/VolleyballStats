package com.kamilh

import com.kamilh.interactors.Synchronizer
import com.kamilh.models.DatabaseConfig
import com.kamilh.models.League
import com.kamilh.models.TestAppConfig
import com.kamilh.storage.DatabaseFactory
import com.kamilh.utils.Logger
import com.kamilh.utils.PlatformLogger
import kotlinx.coroutines.CoroutineScope
import me.tatarka.inject.annotations.Inject
import kotlin.coroutines.coroutineContext

suspend fun main(args: Array<String>) {
    val scope = CoroutineScope(coroutineContext)
    val appModule: AppModule = AppModule::class.create(
        scope = scope,
        appConfig = TestAppConfig(databaseConfig = DatabaseConfig.TEST_DATABASE),
    )

    appModule.initializer.init()
}

@Inject
class TestApplicationInitializer(
    private val platformLogger: PlatformLogger,
    private val databaseFactory: DatabaseFactory,
    private val synchronizer: Synchronizer,
) {

    suspend fun init() {
        Logger.setLogger(platformLogger)
        databaseFactory.connect()
        synchronizer.synchronize(League.POLISH_LEAGUE)
        databaseFactory.close()
    }
}