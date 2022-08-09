package com.kamilh.volleyballstats.presentation.interactors

import com.kamilh.volleyballstats.clients.data.StatsRepository
import com.kamilh.volleyballstats.clients.data.statsRepositoryOf
import com.kamilh.volleyballstats.domain.assertFailure
import com.kamilh.volleyballstats.domain.assertSuccess
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.player.playerOf
import com.kamilh.volleyballstats.domain.tourOf
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.interactors.UpdatePlayersError
import com.kamilh.volleyballstats.interactors.UpdatePlayersParams
import com.kamilh.volleyballstats.network.result.networkFailureOf
import com.kamilh.volleyballstats.network.result.networkSuccessOf
import com.kamilh.volleyballstats.repository.polishleague.networkErrorOf
import com.kamilh.volleyballstats.storage.InsertPlayerError
import com.kamilh.volleyballstats.storage.InsertPlayerResult
import com.kamilh.volleyballstats.storage.PlayerStorage
import com.kamilh.volleyballstats.storage.playerStorageOf
import com.kamilh.volleyballstats.utils.testAppDispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdatePlayersInteractorTest {

    private fun interactor(
        appDispatchers: AppDispatchers = testAppDispatchers,
        playerStorage: PlayerStorage = playerStorageOf(),
        statsRepository: StatsRepository = statsRepositoryOf(),
    ): UpdatePlayersInteractor = UpdatePlayersInteractor(
        appDispatchers = appDispatchers,
        playerStorage = playerStorage,
        statsRepository = statsRepository,
    )

    private fun paramsOf(
        tour: Tour = tourOf(),
    ): UpdatePlayersParams = UpdatePlayersParams(
        tour = tour,
    )

    @Test
    fun `interactor returns Network when getPlayers returns error`() = runTest {
        // GIVEN
        val error = networkErrorOf()

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(
                getPlayers = networkFailureOf(error)
            )
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdatePlayersError.Network)
            assertEquals(error, this.networkError)
        }
    }

    @Test
    fun `interactor returns Storage when getAllPlayers returns error`() = runTest {
        // GIVEN
        val players = listOf(playerOf())
        val error = InsertPlayerError.TourNotFound

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getPlayers = networkSuccessOf(players)),
            playerStorage = playerStorageOf(insert = { _, _ -> InsertPlayerResult.failure(error) }),
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdatePlayersError.Storage)
            assertEquals(error, this.insertPlayerError)
        }
    }

    @Test
    fun `interactor returns Success when repository and storage returns success`() = runTest {
        // GIVEN
        val players = listOf(playerOf())

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getPlayers = networkSuccessOf(players)),
            playerStorage = playerStorageOf(insert = { _, _ -> InsertPlayerResult.success(Unit) }),
        )(paramsOf())

        // THEN
        result.assertSuccess()
    }
}
