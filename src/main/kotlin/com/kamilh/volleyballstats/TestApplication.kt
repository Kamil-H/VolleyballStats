package com.kamilh.volleyballstats

import com.kamilh.volleyballstats.interactors.Synchronizer
import com.kamilh.volleyballstats.models.DatabaseConfig
import com.kamilh.volleyballstats.models.League
import com.kamilh.volleyballstats.models.TestAppConfig
import com.kamilh.volleyballstats.storage.DatabaseFactory
import com.kamilh.volleyballstats.utils.Logger
import com.kamilh.volleyballstats.utils.PlatformLogger
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