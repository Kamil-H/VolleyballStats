package com.kamilh.storage.common

import com.kamilh.models.AppDispatchers
import com.squareup.sqldelight.Transacter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface QueryRunner {
    val dispatcher: CoroutineDispatcher

    suspend fun <T> run(body: () -> T): T

    suspend fun <T> runTransaction(body: () -> T): T
}

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