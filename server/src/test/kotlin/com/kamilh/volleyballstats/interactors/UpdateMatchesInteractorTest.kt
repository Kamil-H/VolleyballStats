package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.domain.models.MatchInfo
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.models.onSuccess
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import com.kamilh.volleyballstats.network.result.networkFailureOf
import com.kamilh.volleyballstats.network.result.networkSuccessOf
import com.kamilh.volleyballstats.repository.polishleague.PlsRepository
import com.kamilh.volleyballstats.repository.polishleague.networkErrorOf
import com.kamilh.volleyballstats.repository.polishleague.plsRepositoryOf
import com.kamilh.volleyballstats.storage.*
import com.kamilh.volleyballstats.utils.testAppDispatchers
import com.kamilh.volleyballstats.utils.testClock
import com.kamilh.volleyballstats.utils.zonedDateTime
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.days

class UpdateMatchesInteractorTest {

    private fun interactor(
        appDispatchers: AppDispatchers = testAppDispatchers,
        tourStorage: TourStorage = tourStorageOf(),
        matchStorage: MatchStorage = matchStorageOf(),
        polishLeagueRepository: PlsRepository = plsRepositoryOf(),
        updateMatchReports: UpdateMatchReports = updateMatchReportsOf(),
    ): UpdateMatchesInteractor = UpdateMatchesInteractor(
        appDispatchers = appDispatchers,
        tourStorage = tourStorage,
        matchStorage = matchStorage,
        polishLeagueRepository = polishLeagueRepository,
        updateMatchReports = updateMatchReports,
    )

    private fun paramsOf(
        tour: Tour = tourOf(),
    ): UpdateMatchesParams = UpdateMatchesParams(
        tour = tour,
    )

    @Before
    fun setClock() {
        CurrentDate.changeClock(testClock)
    }

    @Test
    fun `interactor returns TourNotFound error when getAllMatches returns empty list`() = runTest {
        // GIVEN
        val getAllMatches = listOf(finishedOf())
        val insertOrUpdate = InsertMatchesResult.failure<Unit, InsertMatchesError>(InsertMatchesError.TourNotFound)

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(getAllMatches = networkSuccessOf(getAllMatches)),
            matchStorage = matchStorageOf(insertOrUpdate = insertOrUpdate)
        )(paramsOf())

        // THEN
        result.assertFailure {
            assert(this is UpdateMatchesError.TourNotFound)
        }
    }

    @Test
    fun `interactor returns NoMatchesInTour error when repository's getAllMatches returns empty list`() = runTest {
        // GIVEN
        val getAllMatchInfos = emptyList<MatchInfo>()
        val insertOrUpdate = InsertMatchesResult.failure<Unit, InsertMatchesError>(InsertMatchesError.TourNotFound)

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(getAllMatches = networkSuccessOf(getAllMatchInfos)),
            matchStorage = matchStorageOf(insertOrUpdate = insertOrUpdate)
        )(paramsOf())

        // THEN
        result.assertFailure {
            assert(this is UpdateMatchesError.NoMatchesInTour)
        }
    }

    @Test
    fun `interactor returns SeasonCompleted success when all matches are saved`() = runTest {
        // GIVEN
        val getAllMatchInfos = listOf(finishedOf())
        val getAllMatches = listOf(matchOf(date = zonedDateTime().minus(15.days), hasReport = true))

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(getAllMatches = networkSuccessOf(getAllMatchInfos)),
            matchStorage = matchStorageOf(getAllMatches = flowOf(getAllMatches))
        )(paramsOf())

        // THEN
        result.assertSuccess {
            assert(this is UpdateMatchesSuccess.SeasonCompleted)
        }
    }

    @Test
    fun `last's match date is saved as a the end of tour`() = runTest {
        // GIVEN
        val dates = (0..2).map { index -> zonedDateTime().minus(((index + 3) * 7).days) }
        val getAllMatchInfos = listOf(finishedOf())
        val getAllMatches = dates.map { date -> matchOf(date = date, hasReport = true) }
        var onUpdate: Pair<Tour, LocalDate>? = null
        val params = paramsOf()

        // WHEN
        val result = interactor(
            tourStorage = tourStorageOf(
                onUpdate = { newTour, endTime -> onUpdate = newTour to endTime },
            ),
            polishLeagueRepository = plsRepositoryOf(getAllMatches = networkSuccessOf(getAllMatchInfos)),
            matchStorage = matchStorageOf(getAllMatches = flowOf(getAllMatches))
        )(params)

        // THEN
        result.assertSuccess {
            assert(this is UpdateMatchesSuccess.SeasonCompleted)
        }
        assert(onUpdate?.first == params.tour)
        assert(onUpdate?.second == dates.first().toLocalDate())
    }

    @Test
    fun `interactor returns Network error when updateMatchReports returns Network error`() = runTest {
        // GIVEN
        val getAllMatchInfos = listOf(potentiallyFinishedOf())
        val networkError = networkErrorOf()
        val getAllMatches = listOf(matchOf())

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(getAllMatches = networkSuccessOf(getAllMatchInfos)),
            matchStorage = matchStorageOf(getAllMatches = flowOf(getAllMatches)),
            updateMatchReports = updateMatchReportsOf(
                invoke = { UpdateMatchReportResult.failure(UpdateMatchReportError(networkError)) }
            )
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdateMatchesError.UpdateMatchReportError)
            assert(this.networkErrors.contains(networkError))
        }
    }

    @Test
    fun `interactor returns Insert error when updateMatchReports returns Insert error`() = runTest {
        // GIVEN
        val getAllMatchInfos = listOf(potentiallyFinishedOf())
        val insertError = InsertMatchReportError.TourNotFound
        val getAllMatches = listOf(matchOf())

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(getAllMatches = networkSuccessOf(getAllMatchInfos)),
            matchStorage = matchStorageOf(getAllMatches = flowOf(getAllMatches)),
            updateMatchReports = updateMatchReportsOf(
                invoke = { UpdateMatchReportResult.failure(UpdateMatchReportError(insertError)) }
            )
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdateMatchesError.UpdateMatchReportError)
            assert(this.insertErrors.contains(insertError))
        }
    }

    @Test
    fun `interactor returns NextMatch success when there is some Scheduled match`() = runTest {
        // GIVEN
        val dates = (0..2).map { index -> zonedDateTime().minus(index.days) }
        val getAllMatches = dates.map { date -> matchOf(date = date) }

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(getAllMatches = networkFailureOf(networkErrorOf())),
            matchStorage = matchStorageOf(getAllMatches = flowOf(getAllMatches)),
            updateMatchReports = updateMatchReportsOf(invoke = { UpdateMatchReportResult.success(Unit) })
        )(paramsOf())

        // THEN
        result.onSuccess {
            require(it is UpdateMatchesSuccess.NextMatch)
            assert(it.dateTime == dates.last())
        }
    }

    @Test
    fun `interactor returns NothingToSchedule success when there is no Scheduled match`() = runTest {
        // GIVEN
        val getAllMatcheInfos = listOf(potentiallyFinishedOf(), notScheduledOf(), finishedOf())

        // WHEN
        val result = interactor(
            polishLeagueRepository = plsRepositoryOf(getAllMatches = networkSuccessOf(getAllMatcheInfos)),
            updateMatchReports = updateMatchReportsOf(invoke = { UpdateMatchReportResult.success(Unit) })
        )(paramsOf())

        // THEN
        result.onSuccess {
            require(it is UpdateMatchesSuccess.NothingToSchedule)
        }
    }
}

fun updateMatchReportsOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: (params: UpdateMatchReportParams) -> UpdateMatchReportResult = { UpdateMatchReportResult.success(Unit) },
): UpdateMatchReports = object : UpdateMatchReports(appDispatchers) {

    override suspend fun doWork(params: UpdateMatchReportParams): UpdateMatchReportResult = invoke(params)
}
