package com.kamilh.volleyballstats.storage

import com.squareup.sqldelight.db.SqlDriver

interface SqlDriverFactory {
	fun create(): SqlDriver
}