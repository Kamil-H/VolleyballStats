package com.kamilh.volleyballstats.presentation.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import me.tatarka.inject.annotations.Inject

@Inject
actual class DependencyFactory {
    actual fun createSqlDriver(databaseName: String): SqlDriver =
        JdbcSqliteDriver(url = "jdbc:sqlite:data_files/database.db")
}
