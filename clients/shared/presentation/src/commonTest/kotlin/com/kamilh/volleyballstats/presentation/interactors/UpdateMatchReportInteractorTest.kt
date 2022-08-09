package com.kamilh.volleyballstats.presentation.interactors

import com.kamilh.volleyballstats.clients.data.StatsRepository
import com.kamilh.volleyballstats.clients.data.statsRepositoryOf
import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.interactors.UpdateMatchReportError
import com.kamilh.volleyballstats.interactors.UpdateMatchReportParams
import com.kamilh.volleyballstats.network.result.networkFailureOf
import com.kamilh.volleyballstats.network.result.networkSuccessOf
import com.kamilh.volleyballstats.repository.polishleague.networkErrorOf
import com.kamilh.volleyballstats.storage.InsertMatchReportError
import com.kamilh.volleyballstats.storage.InsertMatchReportResult
import com.kamilh.volleyballstats.storage.MatchReportStorage
import com.kamilh.volleyballstats.storage.matchReportStorageOf
import com.kamilh.volleyballstats.utils.testAppDispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateMatchReportInteractorTest {

    private fun interactor(
        appDispatchers: AppDispatchers = testAppDispatchers,
        statsRepository: StatsRepository = statsRepositoryOf(),
        matchReportStorage: MatchReportStorage = matchReportStorageOf(),
    ): UpdateMatchReportInteractor = UpdateMatchReportInteractor(
        appDispatchers = appDispatchers,
        statsRepository = statsRepository,
        matchReportStorage = matchReportStorage,
    )

    private fun paramsOf(
        tour: Tour = tourOf(),
        matches: List<MatchId> = emptyList(),
    ): UpdateMatchReportParams = UpdateMatchReportParams(
        tour = tour,
        matches = matches,
    )

    @Test
    fun `interactor returns success when there are no match ids on the list`() = runTest {
        // GIVEN
        val matches = emptyList<MatchId>()

        // WHEN
        val result = interactor()(params = paramsOf(matches = matches))

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `interactor returns Network error when getMatchReport returns error`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatchReport = networkFailureOf(networkErrorOf()))
        )(params = paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            require(this is UpdateMatchReportError.Network)
        }
    }

    @Test
    fun `interactor returns Storage error when insert returns error`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())
        val error = InsertMatchReportError.TourNotFound

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatchReport = networkSuccessOf(matchReportOf())),
            matchReportStorage = matchReportStorageOf(
                insert = { _, _ -> InsertMatchReportResult.failure(error) }
            ),
        )(params = paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            require(this is UpdateMatchReportError.Insert)
            assertEquals(error, this.error)
        }
    }

    @Test
    fun `interactor returns Success when get and insert succeeds`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatchReport = networkSuccessOf(matchReportOf())),
            matchReportStorage = matchReportStorageOf(
                insert = { _, _ -> InsertMatchReportResult.success(Unit) }
            ),
        )(params = paramsOf(matches = matches))

        // THEN
        result.assertSuccess()
    }
}
