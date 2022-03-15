package com.kamilh.interactors

import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.repository.polishleague.networkErrorOf
import com.kamilh.repository.polishleague.polishLeagueRepositoryOf
import com.kamilh.storage.*
import com.kamilh.utils.testAppDispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateToursInteractorTest {

    private fun interactor(
        appDispatchers: AppDispatchers = testAppDispatchers,
        polishLeagueRepository: PolishLeagueRepository = polishLeagueRepositoryOf(),
        tourStorage: TourStorage = tourStorageOf(),
        leagueStorage: LeagueStorage = leagueStorageOf(),
    ): UpdateToursInteractor = UpdateToursInteractor(
        appDispatchers = appDispatchers,
        polishLeagueRepository = polishLeagueRepository,
        tourStorage = tourStorage,
        leagueStorage = leagueStorage,
    )

    private fun paramsOf(league: League = leagueOf()): UpdateToursParams = UpdateToursParams(league = league)

    @Test
    fun `interactor returns Network when getAllTours returns error`() = runTest {
        // GIVEN
        val error = networkErrorOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllTours = networkFailureOf(error)
            ),
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdateToursError.Network)
            assert(this.networkError == error)
        }
    }

    @Test
    fun `interactor returns Success when all returns Success`() = runTest {
        // GIVEN
        val tour = tourOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllTours = networkSuccessOf(listOf(tour))
            ),
            tourStorage = tourStorageOf(insert = { InsertTourResult.success(Unit) })
        )(paramsOf())

        // THEN
        result.assertSuccess { }
    }

    @Test
    fun `interactor returns Success getAllTeams return empty list`() = runTest {
        // GIVEN
        val tours = emptyList<Tour>()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllTours = networkSuccessOf(tours)
            ),
            tourStorage = tourStorageOf(insert = { InsertTourResult.success(Unit) })
        )(paramsOf())

        // THEN
        result.assertSuccess { }
    }

    @Test
    fun `interactor tries to insert tour twice when insert league fails`() = runTest {
        // GIVEN
        val tour = tourOf()
        var callCount = 0
        val insert: (tour: Tour) -> InsertTourResult = {
            callCount++
            InsertTourResult.failure(InsertTourError.LeagueNotFound)
        }

        // WHEN
        interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllTours = networkSuccessOf(listOf(tour))
            ),
            tourStorage = tourStorageOf(insert = insert),
            leagueStorage = leagueStorageOf(insert = InsertLeagueResult.success(Unit))
        )(paramsOf())

        // THEN
        assert(callCount == 2)
    }

    @Test
    fun `interactor returns Success when insert tour returns TourAlreadyExists`() = runTest {
        // GIVEN
        val tour = tourOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllTours = networkSuccessOf(listOf(tour))
            ),
            tourStorage = tourStorageOf(insert = { InsertTourResult.failure(InsertTourError.TourAlreadyExists) })
        )(paramsOf())

        // THEN
        result.assertSuccess { }
    }
}

fun updateToursOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: (params: UpdateToursParams) -> UpdateToursResult = { UpdateToursResult.success(Unit) },
): UpdateTours = object : UpdateTours(appDispatchers) {

    override suspend fun doWork(params: UpdateToursParams): UpdateToursResult = invoke(params)
}