package com.kamilh.volleyballstats.storage

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver

internal actual fun <T : QueryResult.Value<Unit>> driver(schema: SqlSchema<T>, name: String): SqlDriver =
    NativeSqliteDriver(schema = schema, name)
