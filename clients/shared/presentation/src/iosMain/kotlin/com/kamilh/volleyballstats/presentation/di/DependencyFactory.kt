package com.kamilh.volleyballstats.presentation.di

import com.kamilh.volleyballstats.storage.Database
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import me.tatarka.inject.annotations.Inject

@Inject
actual class DependencyFactory {

    actual fun createSqlDriver(databaseName: String): SqlDriver =
        NativeSqliteDriver(Database.Schema, databaseName)
}
