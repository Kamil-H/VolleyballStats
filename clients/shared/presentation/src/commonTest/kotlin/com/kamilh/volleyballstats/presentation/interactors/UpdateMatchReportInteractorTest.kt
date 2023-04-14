package com.kamilh.volleyballstats.presentation.interactors

import com.kamilh.volleyballstats.clients.data.StatsRepository
import com.kamilh.volleyballstats.clients.data.statsRepositoryOf
import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.interactors.*
import com.kamilh.volleyballstats.interactors.test.updatePlayersOf
import com.kamilh.volleyballstats.interactors.test.updateTeamsOf
import com.kamilh.volleyballstats.network.result.networkFailureOf
import com.kamilh.volleyballstats.network.result.networkSuccessOf
import com.kamilh.volleyballstats.repository.polishleague.networkErrorOf
import com.kamilh.volleyballstats.storage.*
import com.kamilh.volleyballstats.utils.testAppDispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UpdateMatchReportInteractorTest {

    private fun interactor(
        appDispatchers: AppDispatchers = testAppDispatchers,
        statsRepository: StatsRepository = statsRepositoryOf(),
        matchReportStorage: MatchReportStorage = matchReportStorageOf(),
        updateTeams: UpdateTeams = updateTeamsOf(),
        updatePlayers: UpdatePlayers = updatePlayersOf(),
    ): UpdateMatchReportInteractor = UpdateMatchReportInteractor(
        appDispatchers = appDispatchers,
        statsRepository = statsRepository,
        matchReportStorage = matchReportStorage,
        updateTeams = updateTeams,
        updatePlayers = updatePlayers,
    )

    private fun paramsOf(
        tour: Tour = tourOf(),
        matches: List<MatchId> = emptyList(),
    ): UpdateMatchReportParams = UpdateMatchReportParams(
        tour = tour,
        matches = matches,
    )

    @Test
    fun `interactor returns success when there are no match ids on the list`() = runTest {
        // GIVEN
        val matches = emptyList<MatchId>()

        // WHEN
        val result = interactor()(params = paramsOf(matches = matches))

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `interactor returns Network error when getMatchReport returns error`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())
        val error = networkErrorOf()

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatchReport = networkFailureOf(error))
        )(params = paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            require(networkErrors.contains(error))
        }
    }

    @Test
    fun `interactor returns Storage error when insert returns error`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())
        val error = InsertMatchReportError.TourNotFound

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatchReport = networkSuccessOf(matchReportOf())),
            matchReportStorage = matchReportStorageOf(
                insert = { _, _ -> InsertMatchReportResult.failure(error) }
            ),
        )(params = paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            require(insertErrors.contains(error))
        }
    }

    @Test
    fun `interactor returns Success when get and insert succeeds`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatchReport = networkSuccessOf(matchReportOf())),
            matchReportStorage = matchReportStorageOf(
                insert = { _, _ -> InsertMatchReportResult.success(Unit) }
            ),
        )(params = paramsOf(matches = matches))

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `interactor returns Network Failure when insert failures with PlayerNotFound and update returns Network`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())
        val networkError = networkErrorOf()
        val insertError = InsertMatchReportError.PlayerNotFound(emptyList())

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatchReport = networkSuccessOf(matchReportOf())),
            matchReportStorage = matchReportStorageOf(
                insert = { _, _ -> InsertMatchReportResult.failure(insertError) }
            ),
            updatePlayers = updatePlayersOf {
                UpdatePlayersResult.failure(UpdatePlayersError.Network(networkError))
            }
        )(params = paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            require(networkErrors.contains(networkError))
        }
    }

    @Test
    fun `interactor returns original Failure when insert failures with PlayerNotFound update returns Storage`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())
        val insertError = InsertMatchReportError.PlayerNotFound(emptyList())

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatchReport = networkSuccessOf(matchReportOf())),
            matchReportStorage = matchReportStorageOf(
                insert = { _, _ -> InsertMatchReportResult.failure(insertError) }
            ),
            updatePlayers = updatePlayersOf {
                UpdatePlayersResult.failure(UpdatePlayersError.Storage(InsertPlayerError.TourNotFound))
            }
        )(params = paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            require(insertErrors.contains(insertError))
        }
    }

    @Test
    fun `interactor returns Success when insert failures with PlayerNotFound and update returns Success`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())
        val insertError = InsertMatchReportError.PlayerNotFound(emptyList())
        var callCount = 0

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatchReport = networkSuccessOf(matchReportOf())),
            matchReportStorage = matchReportStorageOf(
                insert = { _, _ ->
                    val isFirstCall = callCount == 0
                    callCount++
                    if (isFirstCall) {
                        InsertMatchReportResult.failure(insertError)
                    } else {
                        InsertMatchReportResult.success(Unit)
                    }
                }
            ),
            updatePlayers = updatePlayersOf {
                UpdatePlayersResult.success(Unit)
            }
        )(params = paramsOf(matches = matches))

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `interactor returns Network Failure when insert failures with NoPlayersInTeams and update returns Network`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())
        val networkError = networkErrorOf()
        val insertError = InsertMatchReportError.NoPlayersInTeams

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatchReport = networkSuccessOf(matchReportOf())),
            matchReportStorage = matchReportStorageOf(
                insert = { _, _ -> InsertMatchReportResult.failure(insertError) }
            ),
            updatePlayers = updatePlayersOf {
                UpdatePlayersResult.failure(UpdatePlayersError.Network(networkError))
            }
        )(params = paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            require(networkErrors.contains(networkError))
        }
    }

    @Test
    fun `interactor returns original Failure when insert failures with NoPlayersInTeams update returns Storage`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())
        val insertError = InsertMatchReportError.NoPlayersInTeams

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatchReport = networkSuccessOf(matchReportOf())),
            matchReportStorage = matchReportStorageOf(
                insert = { _, _ -> InsertMatchReportResult.failure(insertError) }
            ),
            updatePlayers = updatePlayersOf {
                UpdatePlayersResult.failure(UpdatePlayersError.Storage(InsertPlayerError.TourNotFound))
            }
        )(params = paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            require(insertErrors.contains(insertError))
        }
    }

    @Test
    fun `interactor returns Success when insert failures with NoPlayersInTeams and update returns Success`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())
        val insertError = InsertMatchReportError.NoPlayersInTeams
        var callCount = 0

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatchReport = networkSuccessOf(matchReportOf())),
            matchReportStorage = matchReportStorageOf(
                insert = { _, _ ->
                    val isFirstCall = callCount == 0
                    callCount++
                    if (isFirstCall) {
                        InsertMatchReportResult.failure(insertError)
                    } else {
                        InsertMatchReportResult.success(Unit)
                    }
                }
            ),
            updatePlayers = updatePlayersOf {
                UpdatePlayersResult.success(Unit)
            }
        )(params = paramsOf(matches = matches))

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `interactor returns Network Failure when insert failures with TeamNotFound and update returns Network`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())
        val networkError = networkErrorOf()
        val insertError = InsertMatchReportError.TeamNotFound(teamIdOf())

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatchReport = networkSuccessOf(matchReportOf())),
            matchReportStorage = matchReportStorageOf(
                insert = { _, _ -> InsertMatchReportResult.failure(insertError) }
            ),
            updateTeams = updateTeamsOf {
                UpdateTeamsResult.failure(UpdateTeamsError.Network(networkError))
            }
        )(params = paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            require(networkErrors.contains(networkError))
        }
    }

    @Test
    fun `interactor returns original Failure when insert failures with TeamNotFound update returns Storage`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())
        val insertError = InsertMatchReportError.TeamNotFound(teamIdOf())

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatchReport = networkSuccessOf(matchReportOf())),
            matchReportStorage = matchReportStorageOf(
                insert = { _, _ -> InsertMatchReportResult.failure(insertError) }
            ),
            updateTeams = updateTeamsOf {
                UpdateTeamsResult.failure(UpdateTeamsError.Storage(InsertTeamError.TourNotFound))
            }
        )(params = paramsOf(matches = matches))

        // THEN
        result.assertFailure {
            require(insertErrors.contains(insertError))
        }
    }

    @Test
    fun `interactor returns Success when insert failures with TeamNotFound and update returns Success`() = runTest {
        // GIVEN
        val matches = listOf(matchIdOf())
        val insertError = InsertMatchReportError.TeamNotFound(teamIdOf())
        var callCount = 0

        // WHEN
        val result = interactor(
            statsRepository = statsRepositoryOf(getMatchReport = networkSuccessOf(matchReportOf())),
            matchReportStorage = matchReportStorageOf(
                insert = { _, _ ->
                    val isFirstCall = callCount == 0
                    callCount++
                    if (isFirstCall) {
                        InsertMatchReportResult.failure(insertError)
                    } else {
                        InsertMatchReportResult.success(Unit)
                    }
                }
            ),
            updateTeams = updateTeamsOf {
                UpdateTeamsResult.success(Unit)
            }
        )(params = paramsOf(matches = matches))

        // THEN
        result.assertSuccess()
    }
}
