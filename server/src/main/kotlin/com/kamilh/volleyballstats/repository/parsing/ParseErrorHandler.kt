package com.kamilh.volleyballstats.repository.parsing

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.repository.FileManager
import com.kamilh.volleyballstats.repository.FileMetadata
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

fun interface ParseErrorHandler {
    fun handle(parseError: ParseError)
}

@Inject
@Singleton
class SavableParseErrorHandler(
    private val scope: CoroutineScope,
    private val fileManager: FileManager,
) : ParseErrorHandler {

    override fun handle(parseError: ParseError) {
        scope.launch {
            val now = CurrentDate.localDateTime.toString()
            fileManager.saveTextAsFile(
                content = parseError.content,
                fileMetadata = FileMetadata(
                    name = now,
                    directory = EXCEPTIONS_DIRECTORY_PATH,
                    extension = parseError.extension,
                ),
            )
            fileManager.saveTextAsFile(
                content = parseError.exceptionContent(),
                fileMetadata = FileMetadata(
                    name = now,
                    directory = EXCEPTIONS_DIRECTORY_PATH,
                    extension = FileMetadata.Extension.Text,
                ),
            )
        }
    }

    private fun ParseError.exceptionContent(): String = "${exception.message}\n$exception"

    private val ParseError.extension: FileMetadata.Extension
        get() = when (this) {
            is ParseError.Html -> FileMetadata.Extension.Html
            is ParseError.Json -> FileMetadata.Extension.Json
        }

    companion object {
        private const val EXCEPTIONS_DIRECTORY_PATH = "exceptions"
    }
}