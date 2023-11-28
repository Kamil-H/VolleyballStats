package com.kamilh.volleyballstats.repository.sockettoken

import com.kamilh.volleyballstats.models.AppConfig
import com.kamilh.volleyballstats.repository.FileManager
import com.kamilh.volleyballstats.repository.FileMetadata
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.tatarka.inject.annotations.Inject

@JvmInline
value class SocketToken(val value: String)

interface SocketTokenProvider {

    suspend fun getToken(): SocketToken?

    suspend fun expireToken(token: SocketToken)
}

@Inject
class FileSocketTokenProvider(
    private val appConfig: AppConfig,
    private val fileManager: FileManager,
) : SocketTokenProvider {

    private var cachedToken: SocketToken? = null

    private val invalidTokens = mutableSetOf<SocketToken>()

    private val mutex = Mutex()

    override suspend fun getToken(): SocketToken? = mutex.withLock {
        val token = cachedToken
        if (token != null && !invalidTokens.contains(token)) {
            return null
        }
        val tokenFromFile = getTokenFromFile()
        return if (tokenFromFile == null || tokenFromFile in invalidTokens) {
            null
        } else {
            cachedToken = tokenFromFile
            tokenFromFile
        }
    }

    override suspend fun expireToken(token: SocketToken) = mutex.withLock {
        invalidTokens.add(token)
        if (cachedToken == token) {
            cachedToken = null
        }
    }

    private suspend fun getTokenFromFile(): SocketToken? =
        fileManager.getTextContent(
            FileMetadata(
                name = "socket_token",
                directory = appConfig.workDirPath,
                extension = FileMetadata.Extension.Text,
            )
        )?.let(::SocketToken)
}
