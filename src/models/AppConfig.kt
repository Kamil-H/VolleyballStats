package com.kamilh.models

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.ktor.server.application.*

data class AppConfig(
    val serverConfig: ServerConfig,
    val databaseConfig: DatabaseConfig,
    val accessTokens: List<AccessToken>,
)

data class ServerConfig(
    val port: Int,
)

data class DatabaseConfig(
    val jdbcUrl: String,
) {
    companion object {
        val IN_MEMORY: DatabaseConfig = DatabaseConfig(jdbcUrl = JdbcSqliteDriver.IN_MEMORY)
        val TEST_DATABASE: DatabaseConfig = DatabaseConfig(jdbcUrl = "jdbc:sqlite:test_database.db")
    }
}

fun Application.config(): AppConfig {
    val dbConfig = environment.config.config("ktor.database")
    val deploymentConfig = environment.config.config("ktor.deployment")
    val appConfig = environment.config.config("ktor.application")
    return AppConfig(
        serverConfig = ServerConfig(
            port = deploymentConfig.property("port").getString().toInt(),
        ),
        databaseConfig = DatabaseConfig(
            jdbcUrl = dbConfig.property("jdbcUrl").getString(),
        ),
        accessTokens = appConfig.property("accessTokens").getList().map(::AccessToken),
    )
}

val TEST_ACCESS_TOKEN = AccessToken("application_test")
fun TestAppConfig(
    serverConfig: ServerConfig = ServerConfig(port = 1),
    databaseConfig: DatabaseConfig = DatabaseConfig.IN_MEMORY,
    accessTokens: List<AccessToken> = listOf(TEST_ACCESS_TOKEN),
): AppConfig = AppConfig(
    serverConfig = serverConfig,
    databaseConfig = databaseConfig,
    accessTokens = accessTokens,
)
