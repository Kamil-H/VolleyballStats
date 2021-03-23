package com.kamilh

import com.kamilh.interactors.GetAllSeason
import com.kamilh.interactors.GetAllSeasonParams
import com.kamilh.models.Tour
import kotlinx.coroutines.CoroutineScope
import org.kodein.di.DI
import org.kodein.di.instance
import kotlin.coroutines.coroutineContext

suspend fun main(args: Array<String>) {
    val scope = CoroutineScope(coroutineContext)
    val di = DI {
        import(applicationModule(scope))
    }

    val getAllSeason by di.instance<GetAllSeason>()
    getAllSeason(
        GetAllSeasonParams(
            Tour.create(2020)
        )
    )
}