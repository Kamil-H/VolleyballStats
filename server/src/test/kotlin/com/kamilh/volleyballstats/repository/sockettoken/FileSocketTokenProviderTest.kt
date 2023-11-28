package com.kamilh.volleyballstats.repository.sockettoken

import com.kamilh.volleyballstats.models.AppConfig
import com.kamilh.volleyballstats.models.appConfigOf
import com.kamilh.volleyballstats.repository.FileManager
import com.kamilh.volleyballstats.repository.FileMetadata
import com.kamilh.volleyballstats.repository.fileManagerOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class FileSocketTokenProviderTest {

    private fun socketTokenProviderOf(
        fileManager: FileManager = fileManagerOf(),
        appConfig: AppConfig = appConfigOf(),
    ): SocketTokenProvider = FileSocketTokenProvider(
        appConfig = appConfig,
        fileManager = fileManager,
    )

    @Test
    fun `socket is null when file manager returns null`() = runTest {
        // GIVEN
        val workDir = "workDir"
        val appConfig = appConfigOf(workDirPath = workDir)
        var actualMetadata: FileMetadata? = null
        val expectedMetadata = FileMetadata(
            name = "socket_token", directory = workDir, extension = FileMetadata.Extension.Text,
        )
        val fileManager = fileManagerOf(
            getTextContent = { metadata ->
                actualMetadata = metadata
                null
            }
        )
        val socketTokenProvider = socketTokenProviderOf(fileManager = fileManager, appConfig = appConfig)

        // WHEN
        val result = socketTokenProvider.getToken()

        // THEN
        assert(result == null)
        assertEquals(expected = expectedMetadata, actual = actualMetadata)
    }

    @Test
    fun `socket is cached`() = runTest {
        // GIVEN
        val token = "token"
        var callCounter = 0
        val fileManager = fileManagerOf(
            getTextContent = { metadata ->
                callCounter++
                token
            }
        )
        val socketTokenProvider = socketTokenProviderOf(fileManager = fileManager)

        // WHEN
        socketTokenProvider.getToken()

        // THEN
        assertEquals(expected = 1, actual = callCounter)
    }

    @Test
    fun `socket is returned if it is valid`() = runTest {
        // GIVEN
        val token = "token"
        val fileManager = fileManagerOf(getTextContent = token)
        val socketTokenProvider = socketTokenProviderOf(fileManager = fileManager)

        // WHEN
        val result = socketTokenProvider.getToken()

        // THEN
        assertEquals(expected = SocketToken(token), actual = result)
    }

    @Test
    fun `socket is null when file manager returns token that has already been expired`() = runTest {
        // GIVEN
        val token = "token"
        val fileManager = fileManagerOf(getTextContent = token)
        val socketTokenProvider = socketTokenProviderOf(fileManager = fileManager)

        // WHEN
        socketTokenProvider.expireToken(SocketToken(token))
        val result = socketTokenProvider.getToken()

        // THEN
        assert(result == null)
    }
}