package com.kamilh.volleyballstats.presentation.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.kamilh.volleyballstats.storage.Database
import me.tatarka.inject.annotations.Inject

@Inject
actual class DependencyFactory(private val context: Context) {

    actual fun createSqlDriver(databaseName: String): SqlDriver =
        AndroidSqliteDriver(Database.Schema, context, databaseName)
}
