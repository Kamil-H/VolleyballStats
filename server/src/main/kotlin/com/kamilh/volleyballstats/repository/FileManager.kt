package com.kamilh.volleyballstats.repository

import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.domain.utils.ExceptionLogger
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import java.io.File
import java.io.FileNotFoundException

interface FileManager {

    suspend fun saveTextAsFile(content: String, fileMetadata: FileMetadata): Boolean

    suspend fun getTextContent(fileMetadata: FileMetadata): String?
}

@Inject
class IoFileManager(
    private val appDispatchers: AppDispatchers,
    private val exceptionLogger: ExceptionLogger,
) : FileManager {

    override suspend fun saveTextAsFile(content: String, fileMetadata: FileMetadata): Boolean =
        withContext(appDispatchers.default) {
            try {
                val directory = File(fileMetadata.directory)
                if (!directory.exists()) {
                    val created = create(directory)
                    if (!created) {
                        error("It was impossible to create a directory under: $directory")
                    }
                }
                writeToFile(
                    existingDirectory = directory,
                    fileName = fileMetadata.fileName,
                    content = content,
                )
                true
            } catch (exception: Exception) {
                exceptionLogger.log(exception)
                false
            }
        }

    override suspend fun getTextContent(fileMetadata: FileMetadata): String? =
        withContext(appDispatchers.default) {
            try {
                val directory = File(fileMetadata.directory)
                if (!directory.exists()) {
                    null
                } else {
                    fileInDirectory(directory, fileMetadata.fileName).readText()
                }
            } catch (exception: FileNotFoundException) {
                null
            } catch (exception: Exception) {
                exceptionLogger.log(exception)
                null
            }
        }

    private val FileMetadata.fileName: String
        get() = if (extension != null) {
            "$name${extension.string}"
        } else {
            name
        }

    private fun create(directory: File): Boolean = directory.mkdirs()

    private fun writeToFile(existingDirectory: File, fileName: String, content: String) {
        fileInDirectory(existingDirectory, fileName).writeText(content)
    }

    private fun fileInDirectory(existingDirectory: File, fileName: String): File =
        File("${existingDirectory.absolutePath}/$fileName")
}

data class FileMetadata(
    val name: String,
    val directory: String,
    val extension: Extension?,
) {
    enum class Extension(val string: String) {
        Json(".json"),
        Html(".html"),
        Text(".txt")
    }
}