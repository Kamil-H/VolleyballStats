package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.assertFailure
import com.kamilh.volleyballstats.domain.assertSuccess
import com.kamilh.volleyballstats.domain.matchIdOf
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.MatchReport
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.domain.tourOf
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.matchanalyzer.MatchReportAnalyzer
import com.kamilh.volleyballstats.matchanalyzer.MatchReportAnalyzerError
import com.kamilh.volleyballstats.matchanalyzer.MatchReportAnalyzerResult
import com.kamilh.volleyballstats.matchanalyzer.matchReportAnalyzerOf
import com.kamilh.volleyballstats.models.RawMatchReport
import com.kamilh.volleyballstats.models.matchReportIdOf
import com.kamilh.volleyballstats.models.matchReportOf
import com.kamilh.volleyballstats.models.matchReportTeamOf
import com.kamilh.volleyballstats.storage.InsertMatchReportError
import com.kamilh.volleyballstats.storage.InsertMatchReportResult
import com.kamilh.volleyballstats.storage.MatchReportStorage
import com.kamilh.volleyballstats.storage.matchReportStorageOf
import com.kamilh.volleyballstats.utils.testAppDispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MatchReportPreparerInteractorTest {

    private fun interactor(
        appDispatchers: AppDispatchers = testAppDispatchers,
        matchReportAnalyzer: MatchReportAnalyzer = matchReportAnalyzerOf(),
        matchReportStorage: MatchReportStorage = matchReportStorageOf(),
        fixWrongPlayers: FixWrongPlayers = wrongPlayerFixerOf(),
    ): MatchReportPreparerInteractor = MatchReportPreparerInteractor(
        appDispatchers = appDispatchers,
        matchReportAnalyzer = matchReportAnalyzer,
        matchReportStorage = matchReportStorage,
        fixWrongPlayers = fixWrongPlayers,
    )

    private fun paramsOf(
        matches: List<Pair<MatchId, RawMatchReport>> = emptyList(),
        tour: Tour = tourOf(),
    ): MatchReportPreparerParams = MatchReportPreparerParams(
        matches = matches,
        tour = tour,
    )

    private fun insertCallbackOf(callback: () -> InsertMatchReportResult): (matchReport: MatchReport, tourId: TourId) -> InsertMatchReportResult =
        { _, _ -> callback() }

    @Test
    fun `interactor returns Success when empty matches list is passed`() = runTest {
        // GIVEN
        val matches = emptyList<Pair<MatchId, RawMatchReport>>()

        // WHEN
        val result = interactor()(paramsOf(matches = matches))

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `interactor returns Insert error analyze returns TourNotFound`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf() to matchReportOf())

        // WHEN
        val result = interactor(
            matchReportAnalyzer = matchReportAnalyzerOf(invoke = MatchReportAnalyzerResult.success(com.kamilh.volleyballstats.domain.matchReportOf())),
            matchReportStorage = matchReportStorageOf(
                insert = insertCallbackOf { InsertMatchReportResult.failure(InsertMatchReportError.TourNotFound) }
            )
        )(paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            require(this is MatchReportPreparerError.Insert)
            assert(error == InsertMatchReportError.TourNotFound)
        }
    }

    @Test
    fun `analyze Errors gets ignored`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf() to matchReportOf())
        val error = MatchReportAnalyzerError.WrongSetsCount(matchReportIdOf())

        // WHEN
        val result = interactor(
            matchReportAnalyzer = matchReportAnalyzerOf(invoke = MatchReportAnalyzerResult.failure(error)),
            matchReportStorage = matchReportStorageOf(
                insert = insertCallbackOf { InsertMatchReportResult.failure(InsertMatchReportError.TourNotFound) }
            )
        )(paramsOf(matches = matches))

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `interactor returns Insert error when analyze returns PlayerNotFound and tries to insert only 2 times`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf() to matchReportOf())
        val fixedMatchReportTeam = matchReportTeamOf()
        var numberOfCalls = 0
        val insert = insertCallbackOf {
            numberOfCalls++
            InsertMatchReportResult.failure(
                error = InsertMatchReportError.PlayerNotFound(emptyList())
            )
        }

        // WHEN
        val result = interactor(
            matchReportAnalyzer = matchReportAnalyzerOf(invoke = MatchReportAnalyzerResult.success(com.kamilh.volleyballstats.domain.matchReportOf())),
            matchReportStorage = matchReportStorageOf(insert = insert),
            fixWrongPlayers = wrongPlayerFixerOf(invoke = fixedMatchReportTeam)
        )(paramsOf(matches = matches))

        // THEN
        assert(numberOfCalls == 2)
        result.assertFailure {
            require(this is MatchReportPreparerError.Insert)
            assert(error is InsertMatchReportError.PlayerNotFound)
        }
    }

    @Test
    fun `interactor returns Success when all returns Success`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf() to matchReportOf())
        val insert = insertCallbackOf { InsertMatchReportResult.success(Unit) }

        // WHEN
        val result = interactor(
            matchReportAnalyzer = matchReportAnalyzerOf(invoke = MatchReportAnalyzerResult.success(com.kamilh.volleyballstats.domain.matchReportOf())),
            matchReportStorage = matchReportStorageOf(insert = insert),
        )(paramsOf(matches = matches))

        // THEN
        result.assertSuccess()
    }
}

fun matchReportPreparerOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: MatchReportPreparerResult = MatchReportPreparerResult.success(Unit),
): MatchReportPreparer = object : MatchReportPreparer(appDispatchers) {
    override suspend fun doWork(params: MatchReportPreparerParams): MatchReportPreparerResult = invoke
}