package com.kamilh.volleyballstats.models

fun appConfigOf(
    serverConfig: ServerConfig = ServerConfig(0),
    databaseConfig: DatabaseConfig = DatabaseConfig.IN_MEMORY,
    accessTokens: List<AccessToken> = emptyList(),
    workDirPath: String = "",
): AppConfig = AppConfig(
    serverConfig = serverConfig,
    databaseConfig = databaseConfig,
    accessTokens = accessTokens,
    workDirPath = workDirPath,
)