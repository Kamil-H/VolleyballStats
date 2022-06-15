package com.kamilh.volleyballstats.repository.parsing

import com.kamilh.volleyballstats.domain.utils.CurrentDate
import com.kamilh.volleyballstats.models.AppConfig
import com.kamilh.volleyballstats.models.appConfigOf
import com.kamilh.volleyballstats.repository.FileManager
import com.kamilh.volleyballstats.repository.FileMetadata
import com.kamilh.volleyballstats.repository.fileManagerOf
import com.kamilh.volleyballstats.repository.polishleague.htmlParseErrorOf
import com.kamilh.volleyballstats.repository.polishleague.jsonParseErrorOf
import com.kamilh.volleyballstats.utils.testClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SavableParseErrorHandlerTest {

    private fun handler(
        scope: CoroutineScope,
        fileManager: FileManager = fileManagerOf(),
        appConfig: AppConfig = appConfigOf(),
    ): SavableParseErrorHandler = SavableParseErrorHandler(
        scope = scope,
        fileManager = fileManager,
        appConfig = appConfig,
    )

    @Before
    fun updateClock() {
        CurrentDate.changeClock(testClock)
    }

    @Test
    fun `html parser error is saved correctly`() = runTest {
        // GIVEN
        val errorContent = "content"
        val exceptionMessage = "exceptionMessage"
        val exception = IllegalStateException(exceptionMessage)
        val parseError = htmlParseErrorOf(
            content = errorContent,
            exception = exception,
        )
        val arguments = mutableListOf<Pair<String, FileMetadata>>()
        val saveTextAsFile: (String, FileMetadata) -> Boolean = { content, fileMetadata ->
            arguments.add(content to fileMetadata)
            true
        }
        val workDir = "workDir"

        // WHEN
        handler(
            scope = this,
            fileManager = fileManagerOf(saveTextAsFile = saveTextAsFile),
            appConfig = appConfigOf(workDirPath = workDir)
        ).handle(parseError)
        yield()

        // THEN
        val expectedPath = "$workDir/$EXCEPTIONS_DIRECTORY_PATH"
        arguments[0].let { (content, fileMetadata) ->
            assertEquals(errorContent, content)
            assertEquals(CurrentDate.localDateTime.toString(), fileMetadata.name)
            assertEquals(expectedPath, fileMetadata.directory)
            assertEquals(FileMetadata.Extension.Html, fileMetadata.extension)
        }
        arguments[1].let { (content, fileMetadata) ->
            assertEquals("${exceptionMessage}\n$exception", content)
            assertEquals(CurrentDate.localDateTime.toString(), fileMetadata.name)
            assertEquals(expectedPath, fileMetadata.directory)
            assertEquals(FileMetadata.Extension.Text, fileMetadata.extension)
        }
    }

    @Test
    fun `json parser error is saved correctly`() = runTest {
        // GIVEN
        val errorContent = "content"
        val exceptionMessage = "exceptionMessage"
        val exception = IllegalStateException(exceptionMessage)
        val parseError = jsonParseErrorOf(
            content = errorContent,
            exception = exception,
        )
        val arguments = mutableListOf<Pair<String, FileMetadata>>()
        val saveTextAsFile: (String, FileMetadata) -> Boolean = { content, fileMetadata ->
            arguments.add(content to fileMetadata)
            true
        }
        val workDir = "workDir"

        // WHEN
        handler(
            scope = this,
            fileManager = fileManagerOf(saveTextAsFile = saveTextAsFile),
            appConfig = appConfigOf(workDirPath = workDir)
        ).handle(parseError)
        yield()

        // THEN
        val expectedPath = "$workDir/$EXCEPTIONS_DIRECTORY_PATH"
        arguments[0].let { (content, fileMetadata) ->
            assertEquals(errorContent, content)
            assertEquals(CurrentDate.localDateTime.toString(), fileMetadata.name)
            assertEquals(expectedPath, fileMetadata.directory)
            assertEquals(FileMetadata.Extension.Json, fileMetadata.extension)
        }
        arguments[1].let { (content, fileMetadata) ->
            assertEquals("${exceptionMessage}\n$exception", content)
            assertEquals(CurrentDate.localDateTime.toString(), fileMetadata.name)
            assertEquals(expectedPath, fileMetadata.directory)
            assertEquals(FileMetadata.Extension.Text, fileMetadata.extension)
        }
    }

    companion object {
        private const val EXCEPTIONS_DIRECTORY_PATH = "exceptions"
    }
}