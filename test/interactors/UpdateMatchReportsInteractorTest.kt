package com.kamilh.interactors

import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.repository.polishleague.networkErrorOf
import com.kamilh.repository.polishleague.polishLeagueRepositoryOf
import com.kamilh.repository.polishleague.seasonOf
import com.kamilh.storage.InsertMatchStatisticsError
import com.kamilh.utils.testAppDispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateMatchReportsTest {

    private fun interactor(
        appDispatchers: AppDispatchers = testAppDispatchers,
        polishLeagueRepository: PolishLeagueRepository = polishLeagueRepositoryOf(),
        matchReportPreparer: MatchReportPreparer = matchReportPreparerOf(),
    ): UpdateMatchReportInteractor = UpdateMatchReportInteractor(
        appDispatchers = appDispatchers,
        polishLeagueRepository = polishLeagueRepository,
        matchReportPreparer = matchReportPreparer,
    )

    private fun paramsOf(
        league: League = leagueOf(),
        tour: Season = seasonOf(),
        matches: List<AllMatchesItem.PotentiallyFinished> = emptyList(),
    ): UpdateMatchReportParams = UpdateMatchReportParams(
        league = league,
        season = tour,
        matches = matches,
    )

    @Test
    fun `interactor returns Success when list of matches is empty`() = runTest {
        // GIVEN
        val matches = emptyList<AllMatchesItem.PotentiallyFinished>()

        // WHEN
        val result = interactor()(paramsOf(matches = matches))

        // THEN
        result.assertSuccess { }
    }

    @Test
    fun `interactor returns Network error when getMatchReportId returns error`() = runTest {
        // GIVEN
        val matches = listOf(potentiallyFinishedOf())
        val networkError = networkErrorOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getMatchReportId = networkFailureOf(networkError)
            )
        )(paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            require(this is UpdateMatchReportError.Network)
            assert(this.networkError == networkError)
        }
    }

    @Test
    fun `interactor returns Network error when getMatchReport returns error`() = runTest {
        // GIVEN
        val matches = listOf(potentiallyFinishedOf())
        val networkError = networkErrorOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getMatchReportId = networkSuccessOf(matchReportIdOf()),
                getMatchReport = networkFailureOf(networkError)
            )
        )(paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            require(this is UpdateMatchReportError.Network)
            assert(this.networkError == networkError)
        }
    }

    @Test
    fun `interactor returns Insert error when matchReportPreparer returns error`() = runTest {
        // GIVEN
        val matches = listOf(potentiallyFinishedOf())
        val insertError = InsertMatchStatisticsError.TourNotFound

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getMatchReportId = networkSuccessOf(matchReportIdOf()),
                getMatchReport = networkSuccessOf(matchReportOf()),
            ),
            matchReportPreparer = matchReportPreparerOf(
                invoke = MatchReportPreparerResult.failure(MatchReportPreparerError.Insert(insertError))
            )
        )(paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            require(this is UpdateMatchReportError.Insert)
            assert(this.error == insertError)
        }
    }

    @Test
    fun `interactor returns Success when all operations succeed`() = runTest {
        // GIVEN
        val matches = listOf(potentiallyFinishedOf())
        val networkError = networkErrorOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getMatchReportId = networkSuccessOf(matchReportIdOf()),
                getMatchReport = networkSuccessOf(matchReportOf()),
            ),
            matchReportPreparer = matchReportPreparerOf(
                invoke = MatchReportPreparerResult.success(Unit)
            )
        )(paramsOf(matches = matches))

        // THEN
        result.assertSuccess { }
    }
}

fun updateMatchReportsOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: (params: UpdateMatchReportParams) -> UpdateMatchReportResult = { UpdateMatchReportResult.success(Unit) },
): UpdateMatchReports = object : UpdateMatchReports(appDispatchers) {

    override suspend fun doWork(params: UpdateMatchReportParams): UpdateMatchReportResult = invoke(params)
}