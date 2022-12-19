package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.matchIdOf
import com.kamilh.volleyballstats.domain.matchReportOf
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.MatchReport
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.storage.MatchReportStorage
import com.kamilh.volleyballstats.storage.matchReportStorageOf
import com.kamilh.volleyballstats.utils.testAppDispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetMatchReportInteractorTest {

    private fun interactorOf(
        appDispatchers: AppDispatchers = testAppDispatchers,
        matchReportStorage: MatchReportStorage = matchReportStorageOf(),
    ): GetMatchReportInteractor = GetMatchReportInteractor(
        appDispatchers = appDispatchers,
        matchReportStorage = matchReportStorage,
    )

    @Test
    fun `interactor returns null when storage doesn't contain a value`() = runTest {
        // GIVEN
        val matchId = matchIdOf()
        val matchReportStorage = matchReportStorageOf()

        // WHEN
        val value = interactorOf(matchReportStorage = matchReportStorage)(matchId)

        // THEN
        assertNull(value)
    }

    @Test
    fun `interactor hits storage when requesting for the first time`() = runTest {
        // GIVEN
        val matchId = matchIdOf()
        val matchReport = matchReportOf()
        val matchReportStorage = matchReportStorageOf(
            getMatchReport = { matchReport }
        )

        // WHEN
        val value = interactorOf(matchReportStorage = matchReportStorage)(matchId)

        // THEN
        assertEquals(expected = matchReport, actual = value)
    }

    @Test
    fun `interactor hits cache when requesting for second time`() = runTest {
        // GIVEN
        val matchId = matchIdOf()
        val matchReport = matchReportOf()
        var callCount = 0
        val matchReportStorage = matchReportStorageOf(
            getMatchReport = {
                if (callCount == 0) {
                    matchReport
                } else {
                    error("")
                }.also {
                    callCount++
                }
            }
        )
        val interactor = interactorOf(matchReportStorage = matchReportStorage)

        // WHEN
        interactor(matchId)
        val value = interactor(matchId)

        // THEN
        assertEquals(expected = matchReport, actual = value)
    }
}

fun getMatchReportOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: (MatchId) -> MatchReport? = { null },
): GetMatchReport = object : GetMatchReport(appDispatchers) {
    override suspend fun doWork(params: MatchId): MatchReport? = invoke(params)
}
