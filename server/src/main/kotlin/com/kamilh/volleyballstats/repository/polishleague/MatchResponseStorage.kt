package com.kamilh.volleyballstats.repository.polishleague

import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.models.AppConfig
import com.kamilh.volleyballstats.models.MatchReportId
import com.kamilh.volleyballstats.repository.FileManager
import com.kamilh.volleyballstats.repository.FileMetadata
import com.kamilh.volleyballstats.repository.models.MatchResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

interface MatchResponseStorage {

    suspend fun get(matchReportId: MatchReportId, tour: Season): MatchResponse?

    suspend fun save(matchResponse: MatchResponse, tour: Season)

    suspend fun isSaved(matchReportId: MatchReportId, tour: Season): Boolean
}

@Inject
class FileBasedMatchResponseStorage(
    private val json: Json,
    private val fileManager: FileManager,
    private val appConfig: AppConfig,
) : MatchResponseStorage {

    override suspend fun get(matchReportId: MatchReportId, tour: Season): MatchResponse? =
        fileManager.getTextContent(
            FileMetadata(
                name = matchReportId.fileName,
                directory = tour.directory,
                extension = FileMetadata.Extension.Json,
            )
        )?.let { json.decodeFromString(it) }

    override suspend fun save(matchResponse: MatchResponse, tour: Season) {
        fileManager.saveTextAsFile(
            content = json.encodeToString(matchResponse),
            fileMetadata = FileMetadata(
                name = matchResponse.fileName,
                directory = tour.directory,
                extension = FileMetadata.Extension.Json,
            )
        )
    }

    override suspend fun isSaved(matchReportId: MatchReportId, tour: Season): Boolean =
        get(matchReportId, tour) != null

    private val MatchResponse.fileName: String
        get() = matchId.toString()

    private val MatchReportId.fileName: String
        get() = value.toString()

    private val Season.directory: String
        get() = "${appConfig.workDirPath}/match_reports/plus_liga/$value"
}
