package com.kamilh.repository.parsing

import com.kamilh.repository.FileManager
import com.kamilh.repository.FileMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import repository.parsing.ParseError
import java.time.LocalDateTime

fun interface ParseErrorHandler {
    fun handle(parseError: ParseError)
}

class PrintingParseErrorHandler(
    private val scope: CoroutineScope,
    private val fileManager: FileManager,
) : ParseErrorHandler {

    override fun handle(parseError: ParseError) {
        scope.launch {
            val now = LocalDateTime.now().toString()
            fileManager.saveTextAsFile(
                content = parseError.content,
                fileMetadata = FileMetadata(
                    name = now,
                    directory = ExceptionsPath,
                    extension = parseError.extension,
                ),
            )
            fileManager.saveTextAsFile(
                content = parseError.exceptionContent(),
                fileMetadata = FileMetadata(
                    name = now,
                    directory = ExceptionsPath,
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
        private const val ExceptionsPath = "exceptions"
    }
}