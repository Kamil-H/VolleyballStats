package com.kamilh.volleyballstats.presentation.di

import app.cash.sqldelight.db.SqlDriver

expect class DependencyFactory {

    fun createSqlDriver(databaseName: String): SqlDriver
}
