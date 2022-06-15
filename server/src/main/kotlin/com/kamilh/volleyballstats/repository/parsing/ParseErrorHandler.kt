package com.kamilh.volleyballstats.repository.parsing

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import com.kamilh.volleyballstats.models.AppConfig
import com.kamilh.volleyballstats.repository.FileManager
import com.kamilh.volleyballstats.repository.FileMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

fun interface ParseErrorHandler {
    fun handle(parseError: ParseError)
}

@Inject
@Singleton
class SavableParseErrorHandler(
    appConfig: AppConfig,
    private val scope: CoroutineScope,
    private val fileManager: FileManager,
) : ParseErrorHandler {

    private val exceptionsDirectoryPath = "${appConfig.workDirPath}/exceptions"

    override fun handle(parseError: ParseError) {
        scope.launch {
            val now = CurrentDate.localDateTime.toString()
            fileManager.saveTextAsFile(
                content = parseError.content,
                fileMetadata = FileMetadata(
                    name = now,
                    directory = exceptionsDirectoryPath,
                    extension = parseError.extension,
                ),
            )
            fileManager.saveTextAsFile(
                content = parseError.exceptionContent(),
                fileMetadata = FileMetadata(
                    name = now,
                    directory = exceptionsDirectoryPath,
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
}