package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.assertFailure
import com.kamilh.volleyballstats.domain.assertSuccess
import com.kamilh.volleyballstats.domain.matchIdOf
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.tourOf
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.models.matchReportIdOf
import com.kamilh.volleyballstats.models.matchReportOf
import com.kamilh.volleyballstats.network.result.networkFailureOf
import com.kamilh.volleyballstats.network.result.networkSuccessOf
import com.kamilh.volleyballstats.repository.polishleague.PlsRepository
import com.kamilh.volleyballstats.repository.polishleague.networkErrorOf
import com.kamilh.volleyballstats.repository.polishleague.plsRepositoryOf
import com.kamilh.volleyballstats.storage.InsertMatchReportError
import com.kamilh.volleyballstats.utils.testAppDispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateMatchReportsTest {

    private fun interactor(
        appDispatchers: AppDispatchers = testAppDispatchers,
        polishLeagueRepository: PlsRepository = plsRepositoryOf(),
        matchReportPreparer: MatchReportPreparer = matchReportPreparerOf(),
    ): UpdateMatchReportInteractor = UpdateMatchReportInteractor(
        appDispatchers = appDispatchers,
        polishLeagueRepository = polishLeagueRepository,
        matchReportPreparer = matchReportPreparer,
    )

    private fun paramsOf(
        tour: Tour = tourOf(),
        matches: List<MatchId> = emptyList(),
    ): UpdateMatchReportParams = UpdateMatchReportParams(
        tour = tour,
        matches = matches,
    )

    @Test
    fun `interactor returns Success when list of matches is empty`() = runTest {
        // GIVEN
        val matches = emptyList<MatchId>()

        // WHEN
        val result = interactor()(paramsOf(matches = matches))

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `interactor returns Network error when getMatchReportId returns error`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())
        val networkError = networkErrorOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(
                getMatchReportId = networkFailureOf(networkError)
            )
        )(paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            assert(this.networkErrors.contains(networkError))
        }
    }

    @Test
    fun `interactor returns Network error when getMatchReport returns error`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())
        val networkError = networkErrorOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(
                getMatchReportId = networkSuccessOf(matchReportIdOf()),
                getMatchReport = networkFailureOf(networkError)
            )
        )(paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            assert(this.networkErrors.contains(networkError))
        }
    }

    @Test
    fun `interactor returns Insert error when matchReportPreparer returns error`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())
        val insertError = InsertMatchReportError.TourNotFound

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(
                getMatchReportId = networkSuccessOf(matchReportIdOf()),
                getMatchReport = networkSuccessOf(matchReportOf()),
            ),
            matchReportPreparer = matchReportPreparerOf(
                invoke = MatchReportPreparerResult.failure(MatchReportPreparerError.Insert(insertError))
            )
        )(paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            assert(this.insertErrors.contains(insertError))
        }
    }

    @Test
    fun `interactor returns Success when all operations succeed`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(
                getMatchReportId = networkSuccessOf(matchReportIdOf()),
                getMatchReport = networkSuccessOf(matchReportOf()),
            ),
            matchReportPreparer = matchReportPreparerOf(
                invoke = MatchReportPreparerResult.success(Unit)
            )
        )(paramsOf(matches = matches))

        // THEN
        result.assertSuccess()
    }
}
