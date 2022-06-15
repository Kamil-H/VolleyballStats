package com.kamilh.volleyballstats

import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.utils.Logger
import com.kamilh.volleyballstats.domain.utils.PlatformLogger
import com.kamilh.volleyballstats.interactors.Synchronizer
import com.kamilh.volleyballstats.models.TestAppConfig
import com.kamilh.volleyballstats.storage.DatabaseFactory
import kotlinx.coroutines.CoroutineScope
import me.tatarka.inject.annotations.Inject
import kotlin.coroutines.coroutineContext

suspend fun main() {
    val scope = CoroutineScope(coroutineContext)
    AppModule::class.create(
        scope = scope,
        appConfig = TestAppConfig(),
    ).initializer.init()
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