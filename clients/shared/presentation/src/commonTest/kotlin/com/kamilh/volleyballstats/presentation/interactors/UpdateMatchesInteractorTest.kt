package com.kamilh.volleyballstats.presentation.interactors

import com.kamilh.volleyballstats.clients.data.StatsRepository
import com.kamilh.volleyballstats.clients.data.statsRepositoryOf
import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.domain.models.Match
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import com.kamilh.volleyballstats.interactors.*
import com.kamilh.volleyballstats.network.result.networkFailureOf
import com.kamilh.volleyballstats.network.result.networkSuccessOf
import com.kamilh.volleyballstats.repository.polishleague.networkErrorOf
import com.kamilh.volleyballstats.storage.*
import com.kamilh.volleyballstats.utils.testAppDispatchers
import com.kamilh.volleyballstats.utils.testClock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days

class UpdateMatchesInteractorTest {

    private fun interactor(
        appDispatchers: AppDispatchers = testAppDispatchers,
        tourStorage: TourStorage = tourStorageOf(),
        matchStorage: MatchStorage = matchStorageOf(),
        statsRepository: StatsRepository = statsRepositoryOf(),
        updateMatchReports: UpdateMatchReports = updateMatchReportsOf(),
    ): UpdateMatchesInteractor = UpdateMatchesInteractor(
        appDispatchers = appDispatchers,
        tourStorage = tourStorage,
        matchStorage = matchStorage,
        statsRepository = statsRepository,
        updateMatchReports = updateMatchReports,
    )

    private fun paramsOf(
        tour: Tour = tourOf(),
    ): UpdateMatchesParams = UpdateMatchesParams(
        tour = tour,
    )

    @BeforeTest
    fun setClock() {
        CurrentDate.changeClock(testClock)
    }

    @Test
    fun `interactor returns Network error when getMatches returns error`() = runTest {
        // GIVEN
        val error = networkErrorOf()

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatches = networkFailureOf(networkError = error)),
            matchStorage = matchStorageOf(insertOrUpdate = InsertMatchesResult.success(Unit))
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdateMatchesError.Network)
            assertEquals(error, this.networkError)
        }
    }

    @Test
    fun `interactor returns NoMatchesInTour error when repository's getMatches returns empty list`() = runTest {
        // GIVEN
        val getAllMatches = emptyList<Match>()
        val insertOrUpdate = InsertMatchesResult.failure<Unit, InsertMatchesError>(InsertMatchesError.TourNotFound)

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatches = networkSuccessOf(getAllMatches)),
            matchStorage = matchStorageOf(insertOrUpdate = insertOrUpdate)
        )(paramsOf())

        // THEN
        result.assertFailure {
            assertTrue(this is UpdateMatchesError.NoMatchesInTour)
        }
    }

    @Test
    fun `interactor returns TourNotFound error when getMatches returns empty list`() = runTest {
        // GIVEN
        val getMatches = listOf(matchOf())
        val insertOrUpdate = InsertMatchesResult.failure<Unit, InsertMatchesError>(InsertMatchesError.TourNotFound)

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatches = networkSuccessOf(getMatches)),
            matchStorage = matchStorageOf(insertOrUpdate = insertOrUpdate)
        )(paramsOf())

        // THEN
        result.assertFailure {
            assertTrue(this is UpdateMatchesError.TourNotFound)
        }
    }

    @Test
    fun `interactor returns SeasonCompleted when all matches are saved and getTours returns finished tour`() = runTest {
        // GIVEN
        val tourId = tourIdOf(1)
        val getMatches = listOf(matchOf())
        val getAllMatches = listOf(matchOf(hasReport = true))
        val tour = tourOf(id = tourId)
        val tours = listOf(tour.copy(endDate = CurrentDate.localDate))

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(
                getMatches = networkSuccessOf(getMatches),
                getTours = networkSuccessOf(tours),
            ),
            matchStorage = matchStorageOf(
                insertOrUpdate = InsertMatchesResult.success(Unit),
                getAllMatches = flowOf(getAllMatches),
            )
        )(paramsOf(tour = tour))

        // THEN
        result.assertSuccess {
            assertEquals(UpdateMatchesSuccess.SeasonCompleted, this)
        }
    }

    @Test
    fun `updateMatchReports is being called with matchIds that doesn't have report yet`() = runTest {
        // GIVEN
        val savedMatchId = matchOf(id = matchIdOf(1), hasReport = true)
        val notSavedMatchId = matchOf(id = matchIdOf(2), hasReport = false)
        val getAllMatches = listOf(savedMatchId, notSavedMatchId)
        val tour = tourOf()
        val tours = listOf(tour)
        var updateMatchReportParams: UpdateMatchReportParams? = null

        // WHEN
        interactor(
            statsRepository = statsRepositoryOf(
                getMatches = networkSuccessOf(listOf(matchOf())),
                getTours = networkSuccessOf(tours),
            ),
            matchStorage = matchStorageOf(
                insertOrUpdate = InsertMatchesResult.success(Unit),
                getAllMatches = flowOf(getAllMatches),
            ),
            updateMatchReports = updateMatchReportsOf {
                updateMatchReportParams = it
                UpdateMatchReportResult.success(Unit)
            }
        )(paramsOf(tour = tour))

        // THEN
        assertEquals(listOf(notSavedMatchId.id), updateMatchReportParams?.matches)
    }

    @Test
    fun `interactor returns NextMatch with correct date`() = runTest {
        // GIVEN
        val firstMatch = matchOf(date = CurrentDate.zonedDateTime.plus(1.days))
        val secondMatch = matchOf(date = CurrentDate.zonedDateTime.plus(2.days))
        val getAllMatches = listOf(firstMatch, secondMatch)
        val tour = tourOf()
        val tours = listOf(tour)

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(
                getMatches = networkSuccessOf(getAllMatches),
                getTours = networkSuccessOf(tours),
            ),
            matchStorage = matchStorageOf(
                insertOrUpdate = InsertMatchesResult.success(Unit),
                getAllMatches = flowOf(getAllMatches),
            ),
        )(paramsOf(tour = tour))

        // THEN
        result.assertSuccess {
            require(this is UpdateMatchesSuccess.NextMatch)
            assertEquals(firstMatch.date, this.dateTime)
        }
    }

    @Test
    fun `interactor returns NothingToSchedule with correct date`() = runTest {
        // GIVEN
        val getAllMatches = listOf(matchOf(date = null))
        val tour = tourOf()
        val tours = listOf(tour)

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(
                getMatches = networkSuccessOf(getAllMatches),
                getTours = networkSuccessOf(tours),
            ),
            matchStorage = matchStorageOf(
                insertOrUpdate = InsertMatchesResult.success(Unit),
                getAllMatches = flowOf(getAllMatches),
            ),
        )(paramsOf(tour = tour))

        // THEN
        result.assertSuccess {
            assertEquals(UpdateMatchesSuccess.NothingToSchedule, this)
        }
    }
}

fun updateMatchReportsOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: (params: UpdateMatchReportParams) -> UpdateMatchReportResult = { UpdateMatchReportResult.success(Unit) },
): UpdateMatchReports = object : UpdateMatchReports(appDispatchers) {

    override suspend fun doWork(params: UpdateMatchReportParams): UpdateMatchReportResult = invoke(params)
}
