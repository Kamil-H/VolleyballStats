package com.kamilh.models

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
    val username: String,
    val password: String,
    val driverClassName: String,
    val maxPoolSize: Int,
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
            username = dbConfig.property("username").getString(),
            password = dbConfig.property("password").getString(),
            driverClassName = dbConfig.property("driverClassName").getString(),
            maxPoolSize = dbConfig.property("maxPoolSize").getString().toInt(),
        )
    )
}

fun TestAppConfig(): AppConfig =
    AppConfig(
        serverConfig = ServerConfig(1),
        databaseConfig = DatabaseConfig(
            driverClassName = "org.h2.Driver",
            jdbcUrl = "jdbc:h2:mem:;DATABASE_TO_UPPER=false;MODE=MYSQL",
            maxPoolSize = 2,
            username = "",
            password = ""
        )
    )
