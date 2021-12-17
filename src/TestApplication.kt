package com.kamilh

import com.kamilh.interactors.UpdatePlayers
import com.kamilh.interactors.UpdatePlayersParams
import com.kamilh.models.TestAppConfig
import com.kamilh.models.Tour
import kotlinx.coroutines.CoroutineScope
import org.kodein.di.DI
import org.kodein.di.instance
import kotlin.coroutines.coroutineContext

suspend fun main(args: Array<String>) {
    val scope = CoroutineScope(coroutineContext)
    val di = DI {
        import(applicationModule(scope, TestAppConfig()))
    }

//    val getAllSeason by di.instance<GetAllSeason>()
//    getAllSeason(
//        GetAllSeasonParams(
//            Tour.create(2020)
//        )
//    )
    val updatePlayers by di.instance<UpdatePlayers>()
    val result = updatePlayers(
        UpdatePlayersParams(
            Tour.create(2020)
        )
    )
    println(result)
}