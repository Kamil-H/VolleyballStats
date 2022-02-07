package com.kamilh

import com.kamilh.interactors.Synchronizer
import com.kamilh.models.DatabaseConfig
import com.kamilh.models.League
import com.kamilh.models.TestAppConfig
import kotlinx.coroutines.CoroutineScope
import org.kodein.di.DI
import org.kodein.di.instance
import storage.DatabaseFactory
import utils.Logger
import utils.PlatformLogger
import kotlin.coroutines.coroutineContext

suspend fun main(args: Array<String>) {
    val scope = CoroutineScope(coroutineContext)
    val di = DI {
        import(applicationModule(scope, TestAppConfig(databaseConfig = DatabaseConfig.TEST_DATABASE)))
    }
    val platformLogger by di.instance<PlatformLogger>()
    Logger.setLogger(platformLogger)

    val databaseFactory by di.instance<DatabaseFactory>()
    databaseFactory.connect()

//    val updateMatches by di.instance<UpdateMatches>()
//    updateMatches.invoke((UpdateMatchesParams(League.POLISH_LEAGUE, TourYear.create(2020))))

    val synchronizer by di.instance<Synchronizer>()
    synchronizer.synchronize(League.POLISH_LEAGUE)

    databaseFactory.close()
}