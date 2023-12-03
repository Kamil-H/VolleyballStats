package com.kamilh.volleyballstats.storage

import app.cash.sqldelight.db.SqlDriver

interface SqlDriverFactory {
    fun create(): SqlDriver
}
