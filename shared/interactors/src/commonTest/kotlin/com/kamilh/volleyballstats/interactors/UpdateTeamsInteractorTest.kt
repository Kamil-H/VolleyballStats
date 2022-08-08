package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.assertFailure
import com.kamilh.volleyballstats.domain.assertSuccess
import com.kamilh.volleyballstats.domain.models.Team
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.teamOf
import com.kamilh.volleyballstats.domain.tourOf
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.network.repository.PolishLeagueRepository
import com.kamilh.volleyballstats.network.repository.polishLeagueRepositoryOf
import com.kamilh.volleyballstats.network.result.networkFailureOf
import com.kamilh.volleyballstats.network.result.networkSuccessOf
import com.kamilh.volleyballstats.repository.polishleague.networkErrorOf
import com.kamilh.volleyballstats.storage.InsertTeamError
import com.kamilh.volleyballstats.storage.InsertTeamResult
import com.kamilh.volleyballstats.storage.TeamStorage
import com.kamilh.volleyballstats.storage.teamStorageOf
import com.kamilh.volleyballstats.utils.testAppDispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateTeamsInteractorTest {

    private fun interactor(
        appDispatchers: AppDispatchers = testAppDispatchers,
        polishLeagueRepository: PolishLeagueRepository = polishLeagueRepositoryOf(),
        teamStorage: TeamStorage = teamStorageOf(),
    ): UpdateTeamsInteractor = UpdateTeamsInteractor(
        appDispatchers = appDispatchers,
        polishLeagueRepository = polishLeagueRepository,
        teamStorage = teamStorage,
    )

    private fun paramsOf(
        tour: Tour = tourOf(),
    ): UpdateTeamsParams = UpdateTeamsParams(
        tour = tour,
    )

    @Test
    fun `interactor returns Network when getTeams returns error`() = runTest {
        // GIVEN
        val error = networkErrorOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getTeams = networkFailureOf(error)
            ),
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdateTeamsError.Network)
            assertEquals(error, this.networkError)
        }
    }

    @Test
    fun `interactor returns Success when all returns Success`() = runTest {
        // GIVEN
        val team = teamOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getTeams = networkSuccessOf(listOf(team))
            ),
            teamStorage = teamStorageOf(insert = InsertTeamResult.success(Unit))
        )(paramsOf())

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `interactor returns Success getTeams return empty list`() = runTest {
        // GIVEN
        val teams = emptyList<Team>()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getTeams = networkSuccessOf(teams)
            ),
            teamStorage = teamStorageOf(insert = InsertTeamResult.success(Unit))
        )(paramsOf())

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `interactor returns Insert error when insert returns error`() = runTest {
        // GIVEN
        val error = InsertTeamError.TourNotFound

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getTeams = networkSuccessOf(listOf(teamOf()))
            ),
            teamStorage = teamStorageOf(
                insert = InsertTeamResult.failure(error)
            )
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdateTeamsError.Storage)
            assertEquals(error, this.insertTeamError)
        }
    }
}

fun updateTeamsOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: (params: UpdateTeamsParams) -> UpdateTeamsResult = { UpdateTeamsResult.success(Unit) },
): UpdateTeams = object : UpdateTeams(appDispatchers) {

    override suspend fun doWork(params: UpdateTeamsParams): UpdateTeamsResult = invoke(params)
}