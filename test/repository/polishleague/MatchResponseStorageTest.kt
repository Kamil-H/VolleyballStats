package com.kamilh.repository.polishleague

import com.kamilh.models.MatchReportId
import com.kamilh.models.Season
import com.kamilh.models.matchReportIdOf
import com.kamilh.repository.FileManager
import com.kamilh.repository.FileMetadata
import com.kamilh.repository.fileManagerOf
import com.kamilh.repository.models.MatchResponse
import com.kamilh.repository.models.matchResponseOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class MatchResponseStorageTest {

    private val json = Json {  }
    private fun matchResponseStorageOf(
        fileManager: FileManager = fileManagerOf()
    ): MatchResponseStorage =
        FileBasedMatchResponseStorage(
            json = json,
            fileManager = fileManager,
        )

    @Test
    fun `test if storage get returns null when FileManager returns null`() = runBlocking {
        // GIVEN
        val fileManagerReturns = null

        // WHEN
        val matchResponse = matchResponseStorageOf(
            fileManagerOf(getTextContent = fileManagerReturns)
        ).get(matchReportIdOf(), seasonOf())

        // THEN
        assert(matchResponse == null)
    }

    @Test
    fun `test if storage get returns correct value when FileManager returns MatchReport JSON`() = runBlocking {
        // GIVEN
        val saved = matchResponseOf()
        val fileManagerReturns = json.encodeToString(saved)

        // WHEN
        val matchResponse = matchResponseStorageOf(
            fileManagerOf(getTextContent = fileManagerReturns)
        ).get(matchReportIdOf(), seasonOf())

        // THEN
        assert(matchResponse != null)
    }

    @Test
    fun `test if storage isSaved returns false when FileManager returns null`() = runBlocking {
        // GIVEN
        val fileManagerReturns = null

        // WHEN
        val isSaved = matchResponseStorageOf(
            fileManagerOf(getTextContent = fileManagerReturns)
        ).isSaved(matchReportIdOf(), seasonOf())

        // THEN
        assert(!isSaved)
    }

    @Test
    fun `test if storage isSaved returns true when FileManager returns value`() = runBlocking {
        // GIVEN
        val saved = matchResponseOf()
        val fileManagerReturns = json.encodeToString(saved)

        // WHEN
        val isSaved = matchResponseStorageOf(
            fileManagerOf(getTextContent = fileManagerReturns)
        ).isSaved(matchReportIdOf(), seasonOf())

        // THEN
        assert(isSaved)
    }

    @Test
    fun `test if MatchResponse is saved in the correct directory and under correct name`() = runBlocking {
        // GIVEN
        val matchId = 10
        val tour = 2020
        val matchResponse = matchResponseOf(matchId = matchId)
        var savingFileMetadata: FileMetadata? = null
        val saveTextAsFile = { content: String, fileMetadata: FileMetadata ->
            savingFileMetadata = fileMetadata
            true
        }

        // WHEN
        matchResponseStorageOf(
            fileManagerOf(saveTextAsFile = saveTextAsFile)
        ).save(matchResponse, seasonOf(tour))

        // THEN
        assert(savingFileMetadata?.directory == "match_reports/plus_liga/${tour}")
        assert(savingFileMetadata?.extension == FileMetadata.Extension.Json)
        assert(savingFileMetadata?.name == matchId.toString())
    }
}

fun matchResponseStorageOf(
    get: MatchResponse? = null,
    saveCallback: ((matchResponse: MatchResponse, tour: Season) -> Unit)? = null,
    isSaved: Boolean = false
): MatchResponseStorage = object : MatchResponseStorage {

    override suspend fun get(matchReportId: MatchReportId, tour: Season): MatchResponse? = get

    override suspend fun save(matchResponse: MatchResponse, tour: Season) {
        saveCallback?.invoke(matchResponse, tour)
    }

    override suspend fun isSaved(matchReportId: MatchReportId, tour: Season): Boolean = isSaved
}