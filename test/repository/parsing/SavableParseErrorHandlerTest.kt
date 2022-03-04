package com.kamilh.repository.parsing

import com.kamilh.repository.FileManager
import com.kamilh.repository.FileMetadata
import com.kamilh.repository.fileManagerOf
import com.kamilh.repository.polishleague.htmlParseErrorOf
import com.kamilh.repository.polishleague.jsonParseErrorOf
import com.kamilh.utils.CurrentDate
import com.kamilh.utils.testClock
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
    ): SavableParseErrorHandler = SavableParseErrorHandler(
        scope = scope,
        fileManager = fileManager,
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

        // WHEN
        handler(this, fileManagerOf(saveTextAsFile = saveTextAsFile)).handle(parseError)
        yield()

        // THEN
        arguments[0].let { (content, fileMetadata) ->
            assertEquals(errorContent, content)
            assertEquals(CurrentDate.localDateTime.toString(), fileMetadata.name)
            assertEquals(EXCEPTIONS_DIRECTORY_PATH, fileMetadata.directory)
            assertEquals(FileMetadata.Extension.Html, fileMetadata.extension)
        }
        arguments[1].let { (content, fileMetadata) ->
            assertEquals("${exceptionMessage}\n$exception", content)
            assertEquals(CurrentDate.localDateTime.toString(), fileMetadata.name)
            assertEquals(EXCEPTIONS_DIRECTORY_PATH, fileMetadata.directory)
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

        // WHEN
        handler(this, fileManagerOf(saveTextAsFile = saveTextAsFile)).handle(parseError)
        yield()

        // THEN
        arguments[0].let { (content, fileMetadata) ->
            assertEquals(errorContent, content)
            assertEquals(CurrentDate.localDateTime.toString(), fileMetadata.name)
            assertEquals(EXCEPTIONS_DIRECTORY_PATH, fileMetadata.directory)
            assertEquals(FileMetadata.Extension.Json, fileMetadata.extension)
        }
        arguments[1].let { (content, fileMetadata) ->
            assertEquals("${exceptionMessage}\n$exception", content)
            assertEquals(CurrentDate.localDateTime.toString(), fileMetadata.name)
            assertEquals(EXCEPTIONS_DIRECTORY_PATH, fileMetadata.directory)
            assertEquals(FileMetadata.Extension.Text, fileMetadata.extension)
        }
    }

    companion object {
        private const val EXCEPTIONS_DIRECTORY_PATH = "exceptions"
    }
}