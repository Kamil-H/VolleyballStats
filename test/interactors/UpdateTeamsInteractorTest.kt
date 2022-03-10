package com.kamilh.interactors

import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.repository.polishleague.networkErrorOf
import com.kamilh.repository.polishleague.polishLeagueRepositoryOf
import com.kamilh.storage.InsertTeamError
import com.kamilh.storage.InsertTeamResult
import com.kamilh.storage.TeamStorage
import com.kamilh.storage.teamStorageOf
import com.kamilh.utils.testAppDispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test

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
    fun `interactor returns Network when getAllTeams returns error`() = runBlocking {
        // GIVEN
        val error = networkErrorOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllTeams = networkFailureOf(error)
            ),
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdateTeamsError.Network)
            assert(this.networkError == error)
        }
    }

    @Test
    fun `interactor returns Success when all returns Success`() = runBlocking {
        // GIVEN
        val team = teamOf()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllTeams = networkSuccessOf(listOf(team))
            ),
            teamStorage = teamStorageOf(insert = InsertTeamResult.success(Unit))
        )(paramsOf())

        // THEN
        result.assertSuccess { }
    }

    @Test
    fun `interactor returns Success getAllTeams return empty list`() = runBlocking {
        // GIVEN
        val teams = emptyList<Team>()

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllTeams = networkSuccessOf(teams)
            ),
            teamStorage = teamStorageOf(insert = InsertTeamResult.success(Unit))
        )(paramsOf())

        // THEN
        result.assertSuccess { }
    }

    @Test
    fun `interactor returns Insert error when insert returns error`() = runBlocking {
        // GIVEN
        val error = InsertTeamError.TourNotFound

        // WHEN
        val result = interactor(
            polishLeagueRepository = polishLeagueRepositoryOf(
                getAllTeams = networkSuccessOf(listOf(teamOf()))
            ),
            teamStorage = teamStorageOf(
                insert = InsertTeamResult.failure(error)
            )
        )(paramsOf())

        // THEN
        result.assertFailure {
            require(this is UpdateTeamsError.Storage)
            assert(this.insertTeamError == error)
        }
    }
}

fun updateTeamsOf(
    appDispatchers: AppDispatchers = testAppDispatchers,
    invoke: (params: UpdateTeamsParams) -> UpdateTeamsResult = { UpdateTeamsResult.success(Unit) },
): UpdateTeams = object : UpdateTeams(appDispatchers) {

    override suspend fun doWork(params: UpdateTeamsParams): UpdateTeamsResult = invoke(params)
}