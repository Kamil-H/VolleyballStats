package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.models.*
import com.kamilh.volleyballstats.repository.polishleague.PolishLeagueRepository
import com.kamilh.volleyballstats.repository.polishleague.networkErrorOf
import com.kamilh.volleyballstats.repository.polishleague.polishLeagueRepositoryOf
import com.kamilh.volleyballstats.storage.InsertPlayerError
import com.kamilh.volleyballstats.storage.InsertPlayerResult
import com.kamilh.volleyballstats.storage.PlayerStorage
import com.kamilh.volleyballstats.storage.playerStorageOf
import com.kamilh.volleyballstats.utils.testAppDispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdatePlayersInteractorTest {

    private fun interactor(
        appDispatchers: AppDispatchers = testAppDispatchers,
        playerStorage: PlayerStorage = playerStorageOf(),
        polishLeagueRepository: PolishLeagueRepository = polishLeagueRepositoryOf(),
    ): UpdatePlayersInteractor = UpdatePlayersInteractor(
        appDispatchers = appDispatchers,
        playerStorage = playerStorage,
        polishLeagueRepository = polishLeagueRepository,
    )

    private fun paramsOf(
        tour: Tour = tourOf(),
    ): UpdatePlayersParams = UpdatePlayersParams(
        tour = tour,
    )

    @Test
    fun `interactor returns Network when getAllPlayers returns error`() = runTest {
        // GIVEN
        val error = networkErrorOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllPlayersByTour = networkFailureOf(error)
            )
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdatePlayersError.Network)
            assert(this.networkError == error)
        }
    }

    @Test
    fun `interactor returns Network when getPlayerDetails returns error`() = runTest {
        // GIVEN
        val error = networkErrorOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllPlayersByTour = networkSuccessOf(listOf(teamPlayerOf())),
                getPlayerDetails = networkFailureOf(error)
            )
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdatePlayersError.Network)
            assert(this.networkError == error)
        }
    }

    @Test
    fun `interactor returns Success when getAllPlayers returns empty list`() = runTest {
        // GIVEN
        val getAllPlayersByTour = emptyList<TeamPlayer>()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllPlayersByTour = networkSuccessOf(getAllPlayersByTour),
            )
        )(paramsOf())

        // THEN
        result.assertSuccess { }
    }

    @Test
    fun `interactor returns Success when all returns Success`() = runTest {
        // GIVEN
        val playerDetails = playerDetailsOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllPlayersByTour = networkSuccessOf(listOf(teamPlayerOf())),
                getPlayerDetails = networkSuccessOf(playerDetails)
            ),
            playerStorage = playerStorageOf(
                insert = { _, _ -> InsertPlayerResult.success(Unit) }
            )
        )(paramsOf())

        // THEN
        result.assertSuccess { }
    }

    @Test
    fun `interactor returns Insert error when insert returns error`() = runTest {
        // GIVEN
        val error = InsertPlayerError.TourNotFound

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllPlayersByTour = networkSuccessOf(listOf(teamPlayerOf())),
                getPlayerDetails = networkSuccessOf(playerDetailsOf())
            ),
            playerStorage = playerStorageOf(
                insert = { _, _ -> InsertPlayerResult.failure(error) }
            )
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdatePlayersError.Storage)
            assert(this.insertPlayerError == error)
        }
    }
}

fun updatePlayersOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: (params: UpdatePlayersParams) -> UpdatePlayersResult = { UpdatePlayersResult.success(Unit) },
): UpdatePlayers = object : UpdatePlayers(appDispatchers) {

    override suspend fun doWork(params: UpdatePlayersParams): UpdatePlayersResult = invoke(params)
}