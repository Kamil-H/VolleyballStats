package com.kamilh.interactors

import com.kamilh.datetime.LocalDate
import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.repository.polishleague.networkErrorOf
import com.kamilh.repository.polishleague.polishLeagueRepositoryOf
import com.kamilh.storage.*
import com.kamilh.utils.testAppDispatchers
import com.kamilh.utils.zonedDateTime
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.time.Duration.Companion.days

class UpdateMatchesInteractorTest {

    private fun interactor(
        appDispatchers: AppDispatchers = testAppDispatchers,
        tourStorage: TourStorage = tourStorageOf(),
        matchStorage: MatchStorage = matchStorageOf(),
        polishLeagueRepository: PolishLeagueRepository = polishLeagueRepositoryOf(),
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

    @Test
    fun `interactor returns TourNotFound error when getAllMatches returns empty list`() = runTest {
        // GIVEN
        val getAllMatches = listOf(savedOf())
        val insertOrUpdate = InsertMatchesResult.failure<Unit, InsertMatchesError>(InsertMatchesError.TourNotFound)

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(getAllMatches = networkSuccessOf(getAllMatches)),
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
        val getAllMatches = emptyList<AllMatchesItem>()
        val insertOrUpdate = InsertMatchesResult.failure<Unit, InsertMatchesError>(InsertMatchesError.TourNotFound)

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(getAllMatches = networkSuccessOf(getAllMatches)),
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
        val getAllMatches = listOf(savedOf())

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(getAllMatches = networkFailureOf(networkErrorOf())),
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
        val dates = (0..2).map { index -> zonedDateTime().minus(index.days) }
        val getAllMatches = dates.map { date -> savedOf(endTime = date) }
        var onUpdate: Pair<Tour, LocalDate>? = null
        val params = paramsOf()

        // WHEN
        val result = interactor(
            tourStorage = tourStorageOf(
                onUpdate = { newTour, endTime -> onUpdate = newTour to endTime },
            ),
            polishLeagueRepository = polishLeagueRepositoryOf(getAllMatches = networkFailureOf(networkErrorOf())),
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
        val networkError = networkErrorOf()
        val getAllMatches = listOf(potentiallyFinishedOf())

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(getAllMatches = networkFailureOf(networkErrorOf())),
            matchStorage = matchStorageOf(getAllMatches = flowOf(getAllMatches)),
            updateMatchReports = updateMatchReportsOf(
                invoke = { UpdateMatchReportResult.failure(UpdateMatchReportError.Network(networkError)) }
            )
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdateMatchesError.Network)
            assert(this.networkError == networkError)
        }
    }

    @Test
    fun `interactor returns Insert error when updateMatchReports returns Insert error`() = runTest {
        // GIVEN
        val insertError = InsertMatchStatisticsError.TourNotFound
        val getAllMatches = listOf(potentiallyFinishedOf())

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(getAllMatches = networkFailureOf(networkErrorOf())),
            matchStorage = matchStorageOf(getAllMatches = flowOf(getAllMatches)),
            updateMatchReports = updateMatchReportsOf(
                invoke = { UpdateMatchReportResult.failure(UpdateMatchReportError.Insert(insertError)) }
            )
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdateMatchesError.Insert)
            assert(this.error == insertError)
        }
    }

    @Test
    fun `interactor returns NextMatch success when there is some Scheduled match`() = runTest {
        // GIVEN
        val dates = (0..2).map { index -> zonedDateTime().minus(index.days) }
        val getAllMatches = dates.map { date -> scheduledOf(date = date) }

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(getAllMatches = networkFailureOf(networkErrorOf())),
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
        val getAllMatches = listOf(potentiallyFinishedOf(), notScheduledOf(), savedOf())

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(getAllMatches = networkFailureOf(networkErrorOf())),
            matchStorage = matchStorageOf(getAllMatches = flowOf(getAllMatches)),
            updateMatchReports = updateMatchReportsOf(invoke = { UpdateMatchReportResult.success(Unit) })
        )(paramsOf())

        // THEN
        result.onSuccess {
            require(it is UpdateMatchesSuccess.NothingToSchedule)
        }
    }
}

fun updateMatchesOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: (params: UpdateMatchesParams) -> UpdateMatchesResult = { UpdateMatchesResult.success(UpdateMatchesSuccess.NothingToSchedule) },
): UpdateMatches = object : UpdateMatches(appDispatchers) {

    override suspend fun doWork(params: UpdateMatchesParams): UpdateMatchesResult = invoke(params)
}