package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.assertFailure
import com.kamilh.volleyballstats.domain.assertSuccess
import com.kamilh.volleyballstats.domain.leagueOf
import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.tourOf
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.network.repository.PolishLeagueRepository
import com.kamilh.volleyballstats.network.repository.polishLeagueRepositoryOf
import com.kamilh.volleyballstats.network.result.networkFailureOf
import com.kamilh.volleyballstats.network.result.networkSuccessOf
import com.kamilh.volleyballstats.repository.polishleague.networkErrorOf
import com.kamilh.volleyballstats.storage.*
import com.kamilh.volleyballstats.utils.testAppDispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

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
    fun `interactor returns Network when getTours returns error`() = runTest {
        // GIVEN
        val error = networkErrorOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getTours = networkFailureOf(error)
            ),
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdateToursError.Network)
            assertEquals(error, this.networkError)
        }
    }

    @Test
    fun `interactor returns Success when all returns Success`() = runTest {
        // GIVEN
        val tour = tourOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getTours = networkSuccessOf(listOf(tour))
            ),
            tourStorage = tourStorageOf(insert = { InsertTourResult.success(Unit) })
        )(paramsOf())

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `interactor returns Success getAllTeams return empty list`() = runTest {
        // GIVEN
        val tours = emptyList<Tour>()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getTours = networkSuccessOf(tours)
            ),
            tourStorage = tourStorageOf(insert = { InsertTourResult.success(Unit) })
        )(paramsOf())

        // THEN
        result.assertSuccess()
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
                getTours = networkSuccessOf(listOf(tour))
            ),
            tourStorage = tourStorageOf(insert = insert),
            leagueStorage = leagueStorageOf(insert = InsertLeagueResult.success(Unit))
        )(paramsOf())

        // THEN
        assertEquals(expected = 2, actual = callCount)
    }

    @Test
    fun `interactor returns Success when insert tour returns TourAlreadyExists`() = runTest {
        // GIVEN
        val tour = tourOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getTours = networkSuccessOf(listOf(tour))
            ),
            tourStorage = tourStorageOf(insert = { InsertTourResult.failure(InsertTourError.TourAlreadyExists) })
        )(paramsOf())

        // THEN
        result.assertSuccess()
    }
}

fun updateToursOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: (params: UpdateToursParams) -> UpdateToursResult = { UpdateToursResult.success(Unit) },
): UpdateTours = object : UpdateTours(appDispatchers) {

    override suspend fun doWork(params: UpdateToursParams): UpdateToursResult = invoke(params)
}