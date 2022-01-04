package com.kamilh.models

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.ktor.application.*

data class AppConfig(
    val serverConfig: ServerConfig,
    val databaseConfig: DatabaseConfig,
)

data class ServerConfig(
    val port: Int,
)

data class DatabaseConfig(
    val jdbcUrl: String,
)

fun Application.config(): AppConfig {
    val dbConfig = environment.config.config("ktor.database")
    val deploymentConfig = environment.config.config("ktor.deployment")
    return AppConfig(
        serverConfig = ServerConfig(
            port = deploymentConfig.property("port").getString().toInt(),
        ),
        databaseConfig = DatabaseConfig(
            jdbcUrl = dbConfig.property("jdbcUrl").getString(),
        )
    )
}

fun TestAppConfig(): AppConfig =
    AppConfig(
        serverConfig = ServerConfig(1),
        databaseConfig = DatabaseConfig(
            jdbcUrl = JdbcSqliteDriver.IN_MEMORY,
        )
    )
