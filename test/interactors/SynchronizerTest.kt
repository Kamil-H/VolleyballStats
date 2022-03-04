package com.kamilh.interactors

import com.kamilh.models.*
import com.kamilh.repository.polishleague.networkErrorOf
import com.kamilh.storage.*
import com.kamilh.utils.CurrentDate
import com.kamilh.utils.offsetDateTime
import com.kamilh.utils.testClock
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import models.PlayerWithDetails
import org.junit.Before
import org.junit.Test
import utils.Logger
import utils.Severity
import java.time.LocalDateTime

class SynchronizerTest {

    private fun interactor(
        tourStorage: TourStorage = tourStorageOf(),
        teamStorage: TeamStorage = teamStorageOf(),
        playerStorage: PlayerStorage = playerStorageOf(),
        updateMatches: UpdateMatches = updateMatchesOf(),
        updatePlayers: UpdatePlayers = updatePlayersOf(),
        updateTeams: UpdateTeams = updateTeamsOf(),
        updateTours: UpdateTours = updateToursOf(),
        scheduler: SynchronizeScheduler = SynchronizeScheduler { },
    ): Synchronizer = Synchronizer(
        tourStorage = tourStorage,
        teamStorage = teamStorage,
        playerStorage = playerStorage,
        updateMatches = updateMatches,
        updatePlayers = updatePlayers,
        updateTeams = updateTeams,
        updateTours = updateTours,
        scheduler = scheduler,
    )

    @Before
    fun setClock() {
        CurrentDate.changeClock(testClock)
        Logger.setLogger { severity: Severity, tag: String, message: String ->
            println("${severity.shorthand}/$tag: $message")
        }
    }

    @Test
    fun `scheduler is called when tourStorage returns empty list and updateTours returns Failure`() = runTest {
        // GIVEN
        val tours = emptyList<Tour>()
        val updateToursError = UpdateToursError.Network(networkErrorOf())
        var scheduleDate: LocalDateTime? = null

        // WHEN
        interactor(
            tourStorage = tourStorageOf(
                getAllByLeague = flowOf(tours)
            ),
            updateTours = updateToursOf(
                invoke = { UpdateToursResult.failure(updateToursError) }
            ),
            scheduler = { scheduleDate = it }
        ).synchronize(leagueOf())

        // THEN
        assertDate(scheduleDate)
    }

    @Test
    fun `getAllByLeague called twice when tour successfully inserted`() = runTest {
        // GIVEN
        val tours: MutableList<List<Tour>> = mutableListOf(
            emptyList(), listOf(tourOf())
        )
        val updateTours: UpdateToursResult = UpdateToursResult.success(Unit)

        // WHEN
        interactor(
            tourStorage = tourStorageOf(
                getAllByLeague = flow { emit(tours.removeFirst()) }
            ),
            updateTours = updateToursOf(
                invoke = { updateTours }
            ),
        ).synchronize(leagueOf())

        // THEN
        assert(tours.isEmpty())
    }

    @Test
    fun `updateTeams not called when getAllTeams returns some teams`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllTeams = flowOf(listOf(teamOf()))
        var updateTeamCalled = false

        // WHEN
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateTeams = updateTeamsOf {
                updateTeamCalled = true
                UpdateTeamsResult.success(Unit)
            },
            teamStorage = teamStorageOf(getAllTeams = getAllTeams)
        ).synchronize(leagueOf())

        // THEN
        assert(!updateTeamCalled)
    }

    @Test
    fun `updateTeams called when getAllTeams returns empty list`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllTeams = flowOf(emptyList<Team>())
        var updateTeamCalled = false

        // WHEN
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateTeams = updateTeamsOf {
                updateTeamCalled = true
                UpdateTeamsResult.success(Unit)
            },
            teamStorage = teamStorageOf(getAllTeams = getAllTeams)
        ).synchronize(leagueOf())

        // THEN
        assert(updateTeamCalled)
    }

    @Test
    fun `schedule called when updateTeams returns Network error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllTeams = flowOf(emptyList<Team>())
        var scheduleDate: LocalDateTime? = null

        // WHEN
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateTeams = updateTeamsOf { UpdateTeamsResult.failure(UpdateTeamsError.Network(networkErrorOf())) },
            teamStorage = teamStorageOf(getAllTeams = getAllTeams),
            scheduler = { scheduleDate = it }
        ).synchronize(leagueOf())

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
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateTeams = updateTeamsOf { UpdateTeamsResult.failure(UpdateTeamsError.Storage(InsertTeamError.TourNotFound)) },
            teamStorage = teamStorageOf(getAllTeams = getAllTeams),
            updateTours = updateToursOf {
                updateToursCalled = true
                UpdateToursResult.failure(UpdateToursError.Network(networkErrorOf()))
            },
        ).synchronize(leagueOf())

        // THEN
        assert(updateToursCalled)
    }

    @Test
    fun `updatePlayers not called when getAllPlayers returns some teams`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllPlayers = flowOf(listOf(playerWithDetailsOf()))
        var updateTeamCalled = false

        // WHEN
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updatePlayers = updatePlayersOf {
                updateTeamCalled = true
                UpdatePlayersResult.success(Unit)
            },
            playerStorage = playerStorageOf(getAllPlayers = getAllPlayers)
        ).synchronize(leagueOf())

        // THEN
        assert(!updateTeamCalled)
    }

    @Test
    fun `updatePlayers called when getAllPlayers returns empty list`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllPlayers = flowOf(emptyList<PlayerWithDetails>())
        var updateTeamCalled = false

        // WHEN
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updatePlayers = updatePlayersOf {
                updateTeamCalled = true
                UpdatePlayersResult.success(Unit)
            },
            playerStorage = playerStorageOf(getAllPlayers = getAllPlayers)
        ).synchronize(leagueOf())

        // THEN
        assert(updateTeamCalled)
    }

    @Test
    fun `schedule called when updatePlayers returns Network error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllPlayers = flowOf(emptyList<PlayerWithDetails>())
        var scheduleDate: LocalDateTime? = null

        // WHEN
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updatePlayers = updatePlayersOf { UpdatePlayersResult.failure(UpdatePlayersError.Network(networkErrorOf())) },
            playerStorage = playerStorageOf(getAllPlayers = getAllPlayers),
            scheduler = { scheduleDate = it }
        ).synchronize(leagueOf())

        // THEN
        assertDate(scheduleDate)
    }

    @Test
    fun `updateTours called once again when updatePlayers returns TourNotFound error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllPlayers = flowOf(emptyList<PlayerWithDetails>())
        var updateToursCalled = false

        // WHEN
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updatePlayers = updatePlayersOf { UpdatePlayersResult.failure(UpdatePlayersError.Storage(InsertPlayerError.TourNotFound)) },
            playerStorage = playerStorageOf(getAllPlayers = getAllPlayers),
            updateTours = updateToursOf {
                updateToursCalled = true
                UpdateToursResult.failure(UpdateToursError.Network(networkErrorOf()))
            },
        ).synchronize(leagueOf())

        // THEN
        assert(updateToursCalled)
    }

    @Test
    fun `updateTeams called when updatePlayers returns Errors with teamsNotFound not empty error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val getAllPlayers = flowOf(emptyList<PlayerWithDetails>())
        var updateTeamsCalled = false

        // WHEN
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updatePlayers = updatePlayersOf {
                UpdatePlayersResult.failure(
                    error = UpdatePlayersError.Storage(
                        InsertPlayerError.Errors(teamsNotFound = listOf(teamIdOf()),
                            teamPlayersAlreadyExists = emptyList())
                    )
                )
            },
            playerStorage = playerStorageOf(getAllPlayers = getAllPlayers),
            updateTeams = updateTeamsOf {
                updateTeamsCalled = true
                UpdateTeamsResult.success(Unit)
            },
        ).synchronize(leagueOf())

        // THEN
        assert(updateTeamsCalled)
    }

    @Test
    fun `updateTeams called when updateMatches returns TeamNotFound error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        var updateTeamsCalled = false

        // WHEN
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateMatches = updateMatchesOf {
                UpdateMatchesResult.failure(UpdateMatchesError.Insert(InsertMatchStatisticsError.TeamNotFound(teamIdOf())))
            },
            updateTeams = updateTeamsOf {
                updateTeamsCalled = true
                UpdateTeamsResult.success(Unit)
            },
        ).synchronize(leagueOf())

        // THEN
        assert(updateTeamsCalled)
    }

    @Test
    fun `updateTours called when updateMatches returns InsertMatchStatisticsError's TourNotFound error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        var updateToursCalled = false

        // WHEN
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateMatches = updateMatchesOf {
                UpdateMatchesResult.failure(UpdateMatchesError.Insert(InsertMatchStatisticsError.TourNotFound))
            },
            updateTours = updateToursOf {
                updateToursCalled = true
                UpdateToursResult.failure(UpdateToursError.Network(networkErrorOf()))
            },
        ).synchronize(leagueOf())

        // THEN
        assert(updateToursCalled)
    }

    @Test
    fun `updatePlayers called when updateMatches returns NoPlayersInTeams error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        var updatePlayersCalled = false

        // WHEN
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateMatches = updateMatchesOf {
                UpdateMatchesResult.failure(UpdateMatchesError.Insert(InsertMatchStatisticsError.NoPlayersInTeams))
            },
            updatePlayers = updatePlayersOf {
                updatePlayersCalled = true
                UpdatePlayersResult.success(Unit)
            },
        ).synchronize(leagueOf())

        // THEN
        assert(updatePlayersCalled)
    }

    @Test
    fun `updatePlayers called when updateMatches returns PlayerNotFound error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        var updatePlayersCalled = false

        // WHEN
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateMatches = updateMatchesOf {
                UpdateMatchesResult.failure(UpdateMatchesError.Insert(InsertMatchStatisticsError.PlayerNotFound(emptyList())))
            },
            updatePlayers = updatePlayersOf {
                updatePlayersCalled = true
                UpdatePlayersResult.success(Unit)
            },
        ).synchronize(leagueOf())

        // THEN
        assert(updatePlayersCalled)
    }

    @Test
    fun `updateTours called when updateMatches returns TourNotFound error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        var updateToursCalled = false

        // WHEN
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateMatches = updateMatchesOf {
                UpdateMatchesResult.failure(UpdateMatchesError.TourNotFound)
            },
            updateTours = updateToursOf {
                updateToursCalled = true
                UpdateToursResult.failure(UpdateToursError.Network(networkErrorOf()))
            },
        ).synchronize(leagueOf())

        // THEN
        assert(updateToursCalled)
    }

    @Test
    fun `schedule called when updateMatches returns Network error`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        var scheduleDate: LocalDateTime? = null

        // WHEN
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateMatches = updateMatchesOf {
                UpdateMatchesResult.failure(UpdateMatchesError.Network(networkErrorOf()))
            },
            scheduler = { scheduleDate = it }
        ).synchronize(leagueOf())

        // THEN
        assertDate(scheduleDate)
    }

    @Test
    fun `schedule called when updateMatches returns NextMatch success`() = runTest {
        // GIVEN
        val tours = listOf(tourOf())
        val nextMatch = offsetDateTime().plusDays(2)
        var scheduleDate: LocalDateTime? = null

        // WHEN
        interactor(
            tourStorage = tourStorageOf(getAllByLeague = flowOf(tours)),
            updateMatches = updateMatchesOf {
                UpdateMatchesResult.success(UpdateMatchesSuccess.NextMatch(nextMatch))
            },
            scheduler = { scheduleDate = it }
        ).synchronize(leagueOf())

        // THEN
        println("nextMatch: $nextMatch, nextMatchLocal: ${nextMatch.toLocalDateTime()}, $scheduleDate")
        assert(nextMatch.toLocalDateTime().plusHours(3) == scheduleDate)
    }

    private fun assertDate(date: LocalDateTime?) {
        assert(LocalDateTime.now(testClock).plusHours(1) == date)
    }
}