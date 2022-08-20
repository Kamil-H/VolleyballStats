package com.kamilh.volleyballstats.presentation.di

import com.squareup.sqldelight.db.SqlDriver

expect class DependencyFactory {

    fun createSqlDriver(databaseName: String): SqlDriver
}
