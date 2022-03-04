package com.kamilh.interactors

import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.repository.polishleague.networkErrorOf
import com.kamilh.repository.polishleague.polishLeagueRepositoryOf
import com.kamilh.repository.polishleague.tourYearOf
import com.kamilh.storage.InsertPlayerError
import com.kamilh.storage.InsertPlayerResult
import com.kamilh.storage.PlayerStorage
import com.kamilh.storage.playerStorageOf
import com.kamilh.utils.testAppDispatchers
import kotlinx.coroutines.runBlocking
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
        league: League = leagueOf(),
        tourYear: TourYear = tourYearOf(),
    ): UpdatePlayersParams = UpdatePlayersParams(
        league = league,
        tour = tourYear,
    )

    @Test
    fun `interactor returns Network when getAllPlayers returns error`() = runBlocking {
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
    fun `interactor returns Network when getPlayerDetails returns error`() = runBlocking {
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
    fun `interactor returns Success when getAllPlayers returns empty list`() = runBlocking {
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
    fun `interactor returns Success when all returns Success`() = runBlocking {
        // GIVEN
        val playerDetails = playerDetailsOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllPlayersByTour = networkSuccessOf(listOf(teamPlayerOf())),
                getPlayerDetails = networkSuccessOf(playerDetails)
            ),
            playerStorage = playerStorageOf(
                insert = { _, _, _ -> InsertPlayerResult.success(Unit) }
            )
        )(paramsOf())

        // THEN
        result.assertSuccess { }
    }

    @Test
    fun `interactor returns Insert error when insert returns error`() = runBlocking {
        // GIVEN
        val error = InsertPlayerError.TourNotFound

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllPlayersByTour = networkSuccessOf(listOf(teamPlayerOf())),
                getPlayerDetails = networkSuccessOf(playerDetailsOf())
            ),
            playerStorage = playerStorageOf(
                insert = { _, _, _ -> InsertPlayerResult.failure(error) }
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