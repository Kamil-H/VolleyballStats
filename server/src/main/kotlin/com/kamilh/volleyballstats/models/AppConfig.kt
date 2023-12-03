package com.kamilh.volleyballstats.models

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.kamilh.volleyballstats.api.AccessToken
import io.ktor.server.application.Application

data class AppConfig(
    val serverConfig: ServerConfig,
    val databaseConfig: DatabaseConfig,
    val accessTokens: List<AccessToken>,
    val workDirPath: String,
)

data class ServerConfig(val port: Int)

data class DatabaseConfig(val jdbcUrl: String) {
    companion object {
        val IN_MEMORY: DatabaseConfig = DatabaseConfig(jdbcUrl = JdbcSqliteDriver.IN_MEMORY)
        val TEST_DATABASE: DatabaseConfig = DatabaseConfig(jdbcUrl = "jdbc:sqlite:$WORK_DIR/database.db")
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
        workDirPath = appConfig.property("workDir").getString(),
    )
}

private const val WORK_DIR = "data_files"

@Suppress("FunctionNaming")
fun TestAppConfig(): AppConfig = AppConfig(
    serverConfig = ServerConfig(port = 1),
    databaseConfig = DatabaseConfig.TEST_DATABASE,
    accessTokens = listOf(AccessToken("application_test")),
    workDirPath = WORK_DIR,
)
