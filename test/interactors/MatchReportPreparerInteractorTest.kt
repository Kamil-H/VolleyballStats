package com.kamilh.interactors

import com.kamilh.match_analyzer.MatchReportAnalyzer
import com.kamilh.match_analyzer.MatchReportAnalyzerError
import com.kamilh.match_analyzer.MatchReportAnalyzerResult
import com.kamilh.match_analyzer.matchReportAnalyzerOf
import com.kamilh.models.*
import com.kamilh.storage.InsertMatchStatisticsError
import com.kamilh.storage.InsertMatchStatisticsResult
import com.kamilh.storage.MatchStatisticsStorage
import com.kamilh.storage.matchStatisticsStorageOf
import com.kamilh.utils.testAppDispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MatchReportPreparerInteractorTest {

    private fun interactor(
        appDispatchers: AppDispatchers = testAppDispatchers,
        matchReportAnalyzer: MatchReportAnalyzer = matchReportAnalyzerOf(),
        matchStatisticsStorage: MatchStatisticsStorage = matchStatisticsStorageOf(),
        fixWrongPlayers: FixWrongPlayers = wrongPlayerFixerOf(),
    ): MatchReportPreparerInteractor = MatchReportPreparerInteractor(
        appDispatchers = appDispatchers,
        matchReportAnalyzer = matchReportAnalyzer,
        matchStatisticsStorage = matchStatisticsStorage,
        fixWrongPlayers = fixWrongPlayers,
    )

    private fun paramsOf(
        matches: List<Pair<MatchId, MatchReport>> = emptyList(),
        tour: Tour = tourOf(),
    ): MatchReportPreparerParams = MatchReportPreparerParams(
        matches = matches,
        tour = tour,
    )

    private fun insertCallbackOf(callback: () -> InsertMatchStatisticsResult): (matchStatistics: MatchStatistics, tourId: TourId) -> InsertMatchStatisticsResult =
        { _, _ -> callback() }

    @Test
    fun `interactor returns Success when empty matches list is passed`() = runTest {
        // GIVEN
        val matches = emptyList<Pair<MatchId, MatchReport>>()

        // WHEN
        val result = interactor()(paramsOf(matches = matches))

        // THEN
        result.assertSuccess { }
    }

    @Test
    fun `interactor returns Insert error analyze returns TourNotFound`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf() to matchReportOf())

        // WHEN
        val result = interactor(
            matchReportAnalyzer = matchReportAnalyzerOf(invoke = MatchReportAnalyzerResult.success(matchStatisticsOf())),
            matchStatisticsStorage = matchStatisticsStorageOf(
                insert = insertCallbackOf { InsertMatchStatisticsResult.failure(InsertMatchStatisticsError.TourNotFound) }
            )
        )(paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            require(this is MatchReportPreparerError.Insert)
            assert(error == InsertMatchStatisticsError.TourNotFound)
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
            matchStatisticsStorage = matchStatisticsStorageOf(
                insert = insertCallbackOf { InsertMatchStatisticsResult.failure(InsertMatchStatisticsError.TourNotFound) }
            )
        )(paramsOf(matches = matches))

        // THEN
        result.assertSuccess { }
    }

    @Test
    fun `interactor returns Insert error when analyze returns PlayerNotFound and tries to insert only 2 times`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf() to matchReportOf())
        val fixedMatchReportTeam = matchReportTeamOf()
        var numberOfCalls = 0
        val insert = insertCallbackOf {
            numberOfCalls++
            InsertMatchStatisticsResult.failure(
                error = InsertMatchStatisticsError.PlayerNotFound(emptyList())
            )
        }

        // WHEN
        val result = interactor(
            matchReportAnalyzer = matchReportAnalyzerOf(invoke = MatchReportAnalyzerResult.success(matchStatisticsOf())),
            matchStatisticsStorage = matchStatisticsStorageOf(insert = insert),
            fixWrongPlayers = wrongPlayerFixerOf(invoke = fixedMatchReportTeam)
        )(paramsOf(matches = matches))

        // THEN
        assert(numberOfCalls == 2)
        result.assertFailure {
            require(this is MatchReportPreparerError.Insert)
            assert(error is InsertMatchStatisticsError.PlayerNotFound)
        }
    }

    @Test
    fun `interactor returns Success when all returns Success`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf() to matchReportOf())
        val insert = insertCallbackOf { InsertMatchStatisticsResult.success(Unit) }

        // WHEN
        val result = interactor(
            matchReportAnalyzer = matchReportAnalyzerOf(invoke = MatchReportAnalyzerResult.success(matchStatisticsOf())),
            matchStatisticsStorage = matchStatisticsStorageOf(insert = insert),
        )(paramsOf(matches = matches))

        // THEN
        result.assertSuccess { }
    }
}

fun matchReportPreparerOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: MatchReportPreparerResult = MatchReportPreparerResult.success(Unit),
): MatchReportPreparer = object : MatchReportPreparer(appDispatchers) {
    override suspend fun doWork(params: MatchReportPreparerParams): MatchReportPreparerResult = invoke
}