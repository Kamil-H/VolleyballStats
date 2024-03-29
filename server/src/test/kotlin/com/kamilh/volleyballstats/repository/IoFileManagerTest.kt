package com.kamilh.volleyballstats.repository

import com.kamilh.volleyballstats.domain.appDispatchersOf
import com.kamilh.volleyballstats.domain.utils.ExceptionLogger
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class IoFileManagerTest {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    private fun fileManager(
        exceptionLogger: ExceptionLogger = ExceptionLogger { }
    ): IoFileManager =
        IoFileManager(
            appDispatchers = appDispatchersOf(),
            exceptionLogger = exceptionLogger,
        )

    @Test
    fun `file is getting created with correct content when Extension is null`() = runTest {
        // GIVEN
        val content = "content"
        val fileName = "fileName"
        val directory = temporaryFolder.root.absolutePath
        val fileMetadata = FileMetadata(
            name = fileName,
            directory = directory,
            extension = null,
        )

        // WHEN
        val result = fileManager().saveTextAsFile(content, fileMetadata)

        // THEN
        val file = File("${directory}/$fileName")
        assert(result)
        assert(file.exists())
        assert(file.readText() == content)
    }

    @Test
    fun `file is getting created with correct content when Extension is Html`() = runTest {
        // GIVEN
        val content = "content"
        val fileName = "fileName"
        val directory = temporaryFolder.root.absolutePath
        val extension = FileMetadata.Extension.Html
        val fileMetadata = FileMetadata(
            name = fileName,
            directory = directory,
            extension = extension,
        )

        // WHEN
        val result = fileManager().saveTextAsFile(content, fileMetadata)

        // THEN
        val file = File("${directory}/$fileName${extension.string}")
        assert(result)
        assert(file.exists())
        assert(file.readText() == content)
    }

    @Test
    fun `file is getting created with correct content when Extension is Json`() = runTest {
        // GIVEN
        val content = "content"
        val fileName = "fileName"
        val directory = temporaryFolder.root.absolutePath
        val extension = FileMetadata.Extension.Json
        val fileMetadata = FileMetadata(
            name = fileName,
            directory = directory,
            extension = extension,
        )

        // WHEN
        val result = fileManager().saveTextAsFile(content, fileMetadata)

        // THEN
        val file = File("${directory}/$fileName${extension.string}")
        assert(result)
        assert(file.exists())
        assert(file.readText() == content)
    }

    @Test
    fun `file is getting created with correct content when Extension is Text`() = runTest {
        // GIVEN
        val content = "content"
        val fileName = "fileName"
        val directory = temporaryFolder.root.absolutePath
        val extension = FileMetadata.Extension.Text
        val fileMetadata = FileMetadata(
            name = fileName,
            directory = directory,
            extension = extension,
        )

        // WHEN
        val result = fileManager().saveTextAsFile(content, fileMetadata)

        // THEN
        val file = File("${directory}/$fileName${extension.string}")
        assert(result)
        assert(file.exists())
        assert(file.readText() == content)
    }

    @Test
    fun `file is not getting created when directory is wrong`() = runTest {
        // GIVEN
        val content = "content"
        val fileName = "fileName"
        val directory = ""
        val extension = FileMetadata.Extension.Text
        val fileMetadata = FileMetadata(
            name = fileName,
            directory = directory,
            extension = extension,
        )

        // WHEN
        val result = fileManager().saveTextAsFile(content, fileMetadata)

        // THEN
        val file = File("${directory}/$fileName${extension.string}")
        assert(!result)
        assert(!file.exists())
    }

    @Test
    fun `it's possible to ready content when Extension is null`() = runTest {
        // GIVEN
        val content = "content"
        val fileName = "fileName"
        val directory = temporaryFolder.root.absolutePath
        val extension = null
        val fileMetadata = FileMetadata(
            name = fileName,
            directory = directory,
            extension = extension,
        )
        File("${directory}/$fileName").writeText(content)

        // WHEN
        val result = fileManager().getTextContent(fileMetadata)

        // THEN
        assert(result == content)
    }

    @Test
    fun `it's possible to ready content when Extension is Html`() = runTest {
        // GIVEN
        val content = "content"
        val fileName = "fileName"
        val directory = temporaryFolder.root.absolutePath
        val extension = FileMetadata.Extension.Html
        val fileMetadata = FileMetadata(
            name = fileName,
            directory = directory,
            extension = extension,
        )
        File("${directory}/$fileName${extension.string}").writeText(content)

        // WHEN
        val result = fileManager().getTextContent(fileMetadata)

        // THEN
        assert(result == content)
    }

    @Test
    fun `it's possible to ready content when Extension is Json`() = runTest {
        // GIVEN
        val content = "content"
        val fileName = "fileName"
        val directory = temporaryFolder.root.absolutePath
        val extension = FileMetadata.Extension.Json
        val fileMetadata = FileMetadata(
            name = fileName,
            directory = directory,
            extension = extension,
        )
        File("${directory}/$fileName${extension.string}").writeText(content)

        // WHEN
        val result = fileManager().getTextContent(fileMetadata)

        // THEN
        assert(result == content)
    }

    @Test
    fun `it's possible to ready content when Extension is Text`() = runTest {
        // GIVEN
        val content = "content"
        val fileName = "fileName"
        val directory = temporaryFolder.root.absolutePath
        val extension = FileMetadata.Extension.Text
        val fileMetadata = FileMetadata(
            name = fileName,
            directory = directory,
            extension = extension,
        )
        File("${directory}/$fileName${extension.string}").writeText(content)

        // WHEN
        val result = fileManager().getTextContent(fileMetadata)

        // THEN
        assert(result == content)
    }

    @Test
    fun `it's not possible to ready content when file doesn't exists`() = runTest {
        // GIVEN
        val fileName = "fileName"
        val directory = temporaryFolder.root.absolutePath
        val extension = FileMetadata.Extension.Text
        val fileMetadata = FileMetadata(
            name = fileName,
            directory = directory,
            extension = extension,
        )

        // WHEN
        val result = fileManager().getTextContent(fileMetadata)

        // THEN
        assert(result == null)
    }
}

fun fileManagerOf(
    getTextContent: String? = null,
    saveTextAsFile: (String, FileMetadata) -> Boolean = { _, _ -> false },
): FileManager = object : FileManager {

    override suspend fun saveTextAsFile(content: String, fileMetadata: FileMetadata): Boolean = saveTextAsFile(content, fileMetadata)
    override suspend fun getTextContent(fileMetadata: FileMetadata): String? = getTextContent
}