package com.kamilh.storage.common

import com.kamilh.models.AppDispatchers
import com.squareup.sqldelight.Transacter
import kotlinx.coroutines.withContext

interface QueryRunner {
    suspend fun <T> run(body: () -> T): T

    suspend fun <T> runTransaction(body: () -> T): T
}

class TransacterQueryRunner(
    private val transacter: Transacter,
    private val appDispatchers: AppDispatchers,
) : QueryRunner {

    override suspend fun <T> run(body: () -> T): T =
        withContext(appDispatchers.default) {
            body()
        }

    override suspend fun <T> runTransaction(body: () -> T): T =
        withContext(appDispatchers.default) {
            transacter.transactionWithResult {
                body()
            }
        }
}