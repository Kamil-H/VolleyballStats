package com.kamilh.volleyballstats.presentation.di

import android.content.Context
import com.kamilh.volleyballstats.storage.Database
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.android.AndroidSqliteDriver
import me.tatarka.inject.annotations.Inject

@Inject
actual class DependencyFactory(private val context: Context) {

    actual fun createSqlDriver(databaseName: String): SqlDriver =
        AndroidSqliteDriver(Database.Schema, context, databaseName)
}
