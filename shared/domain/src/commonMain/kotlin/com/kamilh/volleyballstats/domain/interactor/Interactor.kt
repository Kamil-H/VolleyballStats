package com.kamilh.volleyballstats.domain.interactor

import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import kotlinx.coroutines.withContext

abstract class Interactor<in P, out T>(private val appDispatchers: AppDispatchers) {

    protected abstract suspend fun doWork(params: P): T

    suspend operator fun invoke(params: P): T =
        withContext(appDispatchers.default) {
            doWork(params)
        }
}

abstract class NoInputInteractor<out T>(private val appDispatchers: AppDispatchers) {

    protected abstract suspend fun doWork(): T

    suspend operator fun invoke(): T =
        withContext(appDispatchers.default) {
            doWork()
        }
}
