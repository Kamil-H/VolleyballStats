package com.kamilh.repository.polishleague

import com.kamilh.repository.FileManager
import com.kamilh.repository.FileMetadata
import com.kamilh.models.MatchReportId
import com.kamilh.models.Tour
import com.kamilh.repository.models.MatchResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface MatchResponseStorage {

    suspend fun get(matchReportId: MatchReportId, tour: Tour): MatchResponse?

    suspend fun save(matchResponse: MatchResponse, tour: Tour)

    suspend fun isSaved(matchReportId: MatchReportId, tour: Tour): Boolean
}

class FileBasedMatchResponseStorage(
    private val json: Json,
    private val fileManager: FileManager,
) : MatchResponseStorage {

    override suspend fun get(matchReportId: MatchReportId, tour: Tour): MatchResponse? =
        fileManager.getTextContent(
            FileMetadata(
                name = matchReportId.fileName,
                directory = tour.directory,
                extension = FileMetadata.Extension.Json,
            )
        )?.let { json.decodeFromString(it) }

    override suspend fun save(matchResponse: MatchResponse, tour: Tour) {
        fileManager.saveTextAsFile(
            content = json.encodeToString(matchResponse),
            fileMetadata = FileMetadata(
                name = matchResponse.fileName,
                directory = tour.directory,
                extension = FileMetadata.Extension.Json,
            )
        )
    }

    override suspend fun isSaved(matchReportId: MatchReportId, tour: Tour): Boolean =
        get(matchReportId, tour) != null

    private val MatchResponse.fileName: String
        get() = matchId.toString()

    private val MatchReportId.fileName: String
        get() = value.toString()

    private val Tour.directory: String
        get() = "match_reports/plus_liga/$value"
}