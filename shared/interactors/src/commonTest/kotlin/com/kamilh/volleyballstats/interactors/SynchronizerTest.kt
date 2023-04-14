package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.*
import com.kamilh.volleyballstats.domain.interactor.Interactor
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.player.playerOf
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import com.kamilh.volleyballstats.domain.utils.Logger
import com.kamilh.volleyballstats.domain.utils.Severity
import com.kamilh.volleyballstats.interactors.test.updatePlayersOf
import com.kamilh.volleyballstats.interactors.test.updateTeamsOf
import com.kamilh.volleyballstats.repository.polishleague.networkErrorOf
import com.kamilh.volleyballstats.storage.*
import com.kamilh.volleyballstats.utils.testClock
import com.kamilh.volleyballstats.utils.zonedDateTime
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class SynchronizerTest {

    private suspend fun TestScope.testInteractor(
        league: League = leagueOf(),
        tourStorage: TourStorage = tourStorageOf(),
        teamStorage: TeamStorage = teamStorageOf(),
        playerStorage: PlayerStorage = playerStorageOf(),
        // Not using typealias, because it caused Unresolved reference
        updateMatches: Interactor<UpdateMatchesParams, Result<UpdateMatchesSuccess, UpdateMatchesError>> = updateMatchesOf(),
        updatePlayers: Interactor<UpdatePlayersParams, Result<Unit, UpdatePlayersError>> = updatePlayersOf(),
        updateTeams: UpdateTeams = updateTeamsOf(),
        updateTours: UpdateTours = updateToursOf(),
        scheduler: SynchronizeScheduler = synchronizeSchedulerOf { },
        synchronizeStateSender: SynchronizeStateSender = SynchronizeStateSender { },
    ) {
        Synchronizer(
            tourStorage = tourStorage,
            teamStorage = teamStorage,
            playerStorage = playerStorage,
            updateMatches = updateMatches,
            updatePlayers = updatePlayers,
            updateTeams = updateTeams,
            updateTours = updateTours,
            scheduler = scheduler,
            coroutineScope = this,
            synchronizeStateSender = synchronizeStateSender,
        ).synchronize(league)
        yield()
    }

    @BeforeTest
    fun setClock() {
        CurrentDate.changeClock(testClock)
        Logger.setLogger { severity: Severity, tag: String?, message: String ->
            println("${severity.shorthand}/$tag: $message")
        }
    }

    @Test
    fun `scheduler is called when tourStorage returns empty list and updateTours returns Failure`() = runTest {
        // GIVEN
        val tours = emptyList<Tour>()
        val updateToursError = UpdateToursError.Network(networkErrorOf())
        var scheduleDate: ZonedDateTime? = null

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(
                getAllByLeague = flowOf(tours)
            ),
            updateTours = updateToursOf(
                invoke = { UpdateToursResult.failure(updateToursError) }
            ),
            scheduler = synchronizeSchedulerOf { scheduleDate = it.first }
        )

        // THEN
        assertDate(scheduleDate)
    }

    @Test
    fun `updateTours called when getAllByLeague returns empty list`() = runTest {
        // GIVEN
        val tours = emptyList<Tour>()
        var updateToursCalled = false

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateTours = updateToursOf {
                updateToursCalled = true
                successOf(Unit)
            },
        )

        // THEN
        assertTrue(updateToursCalled)
    }

    @Test
    fun `getAllByLeague called twice when tour successfully inserted`() = runTest {
        // GIVEN
        val tours: MutableList<List<Tour>> = mutableListOf(emptyList(), listOf(tourOf()))
        val updateTours: UpdateToursResult = successOf(Unit)

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(
                getAllByLeague = flow { emit(tours.removeFirst()) }
            ),
            updateTours = updateToursOf(
                invoke = { updateTours }
            ),
        )

        // THEN
        assertTrue(tours.isEmpty())
    }

    @Test
    fun `State emitted when initializing tours`() = runTest {
        // GIVEN
        val tours: MutableList<List<Tour>> = mutableListOf(emptyList(), listOf(tourOf()))
        val updateTours: UpdateToursResult = successOf(Unit)
        val states = mutableListOf<SynchronizeState>()

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(
                getAllByLeague = flow { emit(tours.removeFirst()) }
            ),
            updateTours = updateToursOf(
                invoke = { updateTours }
            ),
            synchronizeStateSender = states::add
        )

        // THEN
        assertTrue(states.isNotEmpty())
    }

    @Test
    fun `Error is emitted when synchronizing after initializing tours`() = runTest {
        // GIVEN
        val tours: MutableList<List<Tour>> = mutableListOf(emptyList(), listOf(tourOf()))
        val updateTours: UpdateToursResult = successOf(Unit)
        val states = mutableListOf<SynchronizeState>()

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(
                getAllByLeague = flow { emit(tours.removeFirst()) }
            ),
            updateTours = updateToursOf(
                invoke = { updateTours }
            ),
            updateMatches = updateMatchesOf {
                failureOf(UpdateMatchesError.NoMatchesInTour)
            },
            synchronizeStateSender = states::add
        )

        // THEN
        assertTrue(states.contains(SynchronizeState.Error(type = SynchronizeState.Error.Type.Unexpected)))
    }

    @Test
    fun `Error is emitted when UpdateMatches returns Error`() = runTest {
        // GIVEN
        val states = mutableListOf<SynchronizeState>()

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(
                getAllByLeague = flow { emit(listOf(tourOf())) }
            ),
            updateMatches = updateMatchesOf {
                failureOf(UpdateMatchesError.NoMatchesInTour)
            },
            synchronizeStateSender = states::add
        )

        // THEN
        assertTrue(states.contains(SynchronizeState.Error(type = SynchronizeState.Error.Type.Unexpected)))
    }

    @Test
    fun `updateTours called when getAllByLeague returns finished tours`() = runTest {
        // GIVEN
        val tours = listOf(tourOf(endDate = CurrentDate.localDate))
        var updateToursCalled = false

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateTours = updateToursOf {
                updateToursCalled = true
                successOf(Unit)
            },
        )

        // THEN
        assertTrue(updateToursCalled)
    }

    @Test
    fun `updateTeams not called when getAllTeams returns some teams`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllTeams = flowOf(listOf(teamOf()))
        var updateTeamCalled = false

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateTeams = updateTeamsOf {
                updateTeamCalled = true
                successOf(Unit)
            },
            teamStorage = teamStorageOf(getAllTeams = getAllTeams)
        )

        // THEN
        assertTrue(!updateTeamCalled)
    }

    @Test
    fun `updateTeams called when getAllTeams returns empty list`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllTeams = flowOf(emptyList<Team>())
        var updateTeamCalled = false

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateTeams = updateTeamsOf {
                updateTeamCalled = true
                successOf(Unit)
            },
            teamStorage = teamStorageOf(getAllTeams = getAllTeams)
        )

        // THEN
        assertTrue(updateTeamCalled)
    }

    @Test
    fun `schedule called when updateTeams returns Network error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllTeams = flowOf(emptyList<Team>())
        var scheduleDate: ZonedDateTime? = null

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateTeams = updateTeamsOf { UpdateTeamsResult.failure(UpdateTeamsError.Network(networkErrorOf())) },
            teamStorage = teamStorageOf(getAllTeams = getAllTeams),
            scheduler = synchronizeSchedulerOf { scheduleDate = it.first }
        )

        // THEN
        assertDate(scheduleDate)
    }

    @Test
    fun `updateTours called once again when updateTeams returns TourNotFound error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllTeams = flowOf(emptyList<Team>())
        var updateToursCalled = false

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateTeams = updateTeamsOf { UpdateTeamsResult.failure(UpdateTeamsError.Storage(InsertTeamError.TourNotFound)) },
            teamStorage = teamStorageOf(getAllTeams = getAllTeams),
            updateTours = updateToursOf {
                updateToursCalled = true
                UpdateToursResult.failure(UpdateToursError.Network(networkErrorOf()))
            },
        )

        // THEN
        assertTrue(updateToursCalled)
    }

    @Test
    fun `updatePlayers not called when getAllPlayers returns some players`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllPlayers = flowOf(listOf(playerOf()))
        var updateTeamCalled = false

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updatePlayers = updatePlayersOf {
                updateTeamCalled = true
                successOf(Unit)
            },
            playerStorage = playerStorageOf(getAllPlayers = getAllPlayers)
        )

        // THEN
        assertTrue(!updateTeamCalled)
    }

    @Test
    fun `updatePlayers called when getAllPlayers returns empty list`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllPlayers = flowOf(emptyList<Player>())
        var updateTeamCalled = false

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updatePlayers = updatePlayersOf {
                updateTeamCalled = true
                successOf(Unit)
            },
            playerStorage = playerStorageOf(getAllPlayers = getAllPlayers)
        )

        // THEN
        assertTrue(updateTeamCalled)
    }

    @Test
    fun `schedule called when updatePlayers returns Network error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllPlayers = flowOf(emptyList<Player>())
        var scheduleDate: ZonedDateTime? = null

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updatePlayers = updatePlayersOf { Result.failure(UpdatePlayersError.Network(networkErrorOf())) },
            playerStorage = playerStorageOf(getAllPlayers = getAllPlayers),
            scheduler = synchronizeSchedulerOf { scheduleDate = it.first }
        )

        // THEN
        assertDate(scheduleDate)
    }

    @Test
    fun `updateTours called once again when updatePlayers returns TourNotFound error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllPlayers = flowOf(emptyList<Player>())
        var updateToursCalled = false

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updatePlayers = updatePlayersOf { Result.failure(UpdatePlayersError.Storage(InsertPlayerError.TourNotFound)) },
            playerStorage = playerStorageOf(getAllPlayers = getAllPlayers),
            updateTours = updateToursOf {
                updateToursCalled = true
                UpdateToursResult.failure(UpdateToursError.Network(networkErrorOf()))
            },
        )

        // THEN
        assertTrue(updateToursCalled)
    }

    @Test
    fun `updateTeams called when updatePlayers returns Errors with teamsNotFound not empty error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllPlayers = flowOf(emptyList<Player>())
        var updateTeamsCalled = false

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updatePlayers = updatePlayersOf {
                Result.failure(
                    error = UpdatePlayersError.Storage(
                        InsertPlayerError.Errors(
                            teamsNotFound = listOf(teamIdOf()),
                            teamPlayersAlreadyExists = emptyList()
                        )
                    )
                )
            },
            playerStorage = playerStorageOf(getAllPlayers = getAllPlayers),
            updateTeams = updateTeamsOf {
                updateTeamsCalled = true
                successOf(Unit)
            },
        )

        // THEN
        assertTrue(updateTeamsCalled)
    }

    @Test
    fun `updateTeams called when updateMatches returns TeamNotFound error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        var updateTeamsCalled = false

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateMatches = updateMatchesOf {
                Result.failure(UpdateMatchesError.UpdateMatchReportError(InsertMatchReportError.TeamNotFound(teamIdOf())))
            },
            updateTeams = updateTeamsOf {
                updateTeamsCalled = true
                successOf(Unit)
            },
        )

        // THEN
        assertTrue(updateTeamsCalled)
    }

    @Test
    fun `updateTours called when updateMatches returns InsertMatchReportError's TourNotFound error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        var updateToursCalled = false

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateMatches = updateMatchesOf {
                Result.failure(UpdateMatchesError.UpdateMatchReportError(InsertMatchReportError.TourNotFound))
            },
            updateTours = updateToursOf {
                updateToursCalled = true
                UpdateToursResult.failure(UpdateToursError.Network(networkErrorOf()))
            },
        )

        // THEN
        assertTrue(updateToursCalled)
    }

    @Test
    fun `updatePlayers called when updateMatches returns NoPlayersInTeams error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        var updatePlayersCalled = false

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateMatches = updateMatchesOf {
                Result.failure(UpdateMatchesError.UpdateMatchReportError(InsertMatchReportError.NoPlayersInTeams))
            },
            updatePlayers = updatePlayersOf {
                updatePlayersCalled = true
                successOf(Unit)
            },
        )

        // THEN
        assertTrue(updatePlayersCalled)
    }

    @Test
    fun `updatePlayers called when updateMatches returns PlayerNotFound error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        var updatePlayersCalled = false

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateMatches = updateMatchesOf {
                Result.failure(
                    UpdateMatchesError.UpdateMatchReportError(
                        InsertMatchReportError.PlayerNotFound(
                            emptyList()
                        )
                    )
                )
            },
            updatePlayers = updatePlayersOf {
                updatePlayersCalled = true
                successOf(Unit)
            },
        )

        // THEN
        assertTrue(updatePlayersCalled)
    }

    @Test
    fun `updateTours called when updateMatches returns TourNotFound error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        var updateToursCalled = false

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateMatches = updateMatchesOf {
                Result.failure(UpdateMatchesError.TourNotFound)
            },
            updateTours = updateToursOf {
                updateToursCalled = true
                UpdateToursResult.failure(UpdateToursError.Network(networkErrorOf()))
            },
        )

        // THEN
        assertTrue(updateToursCalled)
    }

    @Test
    fun `schedule called when updateMatches returns Network error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        var scheduleDate: ZonedDateTime? = null

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateMatches = updateMatchesOf {
                Result.failure(UpdateMatchesError.UpdateMatchReportError(networkErrorOf()))
            },
            scheduler = synchronizeSchedulerOf { scheduleDate = it.first }
        )

        // THEN
        assertDate(scheduleDate)
    }

    @Test
    fun `schedule called when updateMatches returns NextMatch success`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val nextMatch = zonedDateTime().plus(2.days)
        var scheduleDate: ZonedDateTime? = null

        // WHEN
        testInteractor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateMatches = updateMatchesOf {
                successOf(UpdateMatchesSuccess.NextMatch(nextMatch))
            },
            scheduler = synchronizeSchedulerOf { scheduleDate = it.first }
        )

        // THEN
        assertEquals(nextMatch.plus(3.hours), scheduleDate)
    }

    private fun assertDate(date: ZonedDateTime?) {
        assertEquals(zonedDateTime().plus(5.minutes), date)
    }
}