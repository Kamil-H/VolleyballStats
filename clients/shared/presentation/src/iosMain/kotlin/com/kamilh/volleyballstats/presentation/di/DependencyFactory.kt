package com.kamilh.volleyballstats.presentation.di

import com.kamilh.volleyballstats.storage.Database
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.drivers.native.NativeSqliteDriver
import me.tatarka.inject.annotations.Inject

@Inject
actual class DependencyFactory {

    actual fun createSqlDriver(databaseName: String): SqlDriver =
        NativeSqliteDriver(Database.Schema, databaseName)
}
