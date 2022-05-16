package com.kamilh.volleyballstats.storage.common

import com.kamilh.volleyballstats.models.AppDispatchers
import com.squareup.sqldelight.Transacter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

interface QueryRunner {
    val dispatcher: CoroutineDispatcher

    suspend fun <T> run(body: () -> T): T

    suspend fun <T> runTransaction(body: () -> T): T
}

@Inject
class TransacterQueryRunner(
    appDispatchers: AppDispatchers,
    private val transacter: Transacter,
) : QueryRunner {

    override val dispatcher: CoroutineDispatcher = appDispatchers.default

    override suspend fun <T> run(body: () -> T): T =
        withContext(dispatcher) {
            body()
        }

    override suspend fun <T> runTransaction(body: () -> T): T =
        withContext(dispatcher) {
            transacter.transactionWithResult {
                body()
            }
        }
}