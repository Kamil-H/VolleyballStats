package com.kamilh

import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
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

//    val synchronizer by di.instance<Synchronizer>()
//    synchronizer.synchronize(League.POLISH_LEAGUE)
//
    val repository by di.instance<PolishLeagueRepository>()
    repository.getAllMatches(tour = TourYear.create(2021))
        .onSuccess {
            println(it.size)
            it.forEach { println(it) }
        }
        .onFailure {
            when (it) {
                is NetworkError.ConnectionError -> TODO()
                NetworkError.HttpError.BadRequestException -> TODO()
                NetworkError.HttpError.Conflict -> TODO()
                NetworkError.HttpError.ForbiddenException -> TODO()
                NetworkError.HttpError.InternalServerErrorException -> TODO()
                NetworkError.HttpError.MethodNotAllowed -> TODO()
                NetworkError.HttpError.NotAcceptable -> TODO()
                NetworkError.HttpError.NotFoundException -> TODO()
                is NetworkError.HttpError.Other -> TODO()
                NetworkError.HttpError.ProxyAuthenticationRequired -> TODO()
                NetworkError.HttpError.RequestTimeout -> TODO()
                NetworkError.HttpError.UnauthorizedException -> TODO()
                is NetworkError.HttpError.UnexpectedException -> TODO()
                is NetworkError.UnexpectedException -> {
                    it.throwable.printStackTrace()
                }
            }
        }

    databaseFactory.close()
}