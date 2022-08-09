package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import com.kamilh.volleyballstats.domain.utils.Logger
import com.kamilh.volleyballstats.network.NetworkError
import com.kamilh.volleyballstats.storage.*
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@Inject
class Synchronizer(
    private val tourStorage: TourStorage,
    private val teamStorage: TeamStorage,
    private val playerStorage: PlayerStorage,
    private val updateMatches: UpdateMatches,
    private val updatePlayers: UpdatePlayers,
    private val updateTeams: UpdateTeams,
    private val updateTours: UpdateTours,
    private val scheduler: SynchronizeScheduler,
) {

    private val tag = this::class.simpleName!!

    suspend fun synchronize(league: League) {
        log("Synchronizing: $league")
        val tours = tourStorage.getAllByLeague(League.POLISH_LEAGUE).first()
        if (tours.isEmpty()) {
            log("Tours are empty")
            initializeTours(league)
        } else {
            updateMatches(tours.filterNot { it.isFinished })
        }
    }

    private suspend fun updateMatches(tours: List<Tour>) {
        tours.forEach { tour ->
            log("Updating matches ${tour.league}, ${tour.season}")
            updateTeams(tour)
            updatePlayers(tour)
            updateMatches(UpdateMatchesParams(tour))
                .onResult { log("Updating matches result: $it") }
                .onFailure { error -> onUpdateMatchesFailure(error, tour) }
                .onSuccess {
                    when (it) {
                        UpdateMatchesSuccess.NothingToSchedule, UpdateMatchesSuccess.SeasonCompleted -> { }
                        is UpdateMatchesSuccess.NextMatch -> schedule(it.dateTime.toLocalDateTime().plus(3.hours))
                    }
                }
        }
    }

    private suspend fun onUpdateMatchesFailure(error: UpdateMatchesError, tour: Tour) {
        when (error) {
            is UpdateMatchesError.Network -> {
                val networkError = error.networkError
                if (networkError is NetworkError.UnexpectedException) {
                    Logger.e(tag = tag, message = networkError.throwable.stackTraceToString())
                }
                schedule()
            }
            UpdateMatchesError.TourNotFound -> initializeTours(tour.league)
            UpdateMatchesError.NoMatchesInTour -> { }
            is UpdateMatchesError.Insert -> when (error.error) {
                InsertMatchReportError.NoPlayersInTeams, is InsertMatchReportError.PlayerNotFound -> updatePlayers(tour)
                is InsertMatchReportError.TeamNotFound -> updateTeams(tour)
                InsertMatchReportError.TourNotFound -> initializeTours(league = tour.league)
            }
        }
    }

    private suspend fun schedule(duration: Duration = 1.hours) {
        schedule(CurrentDate.localDateTime.plus(duration))
    }

    private suspend fun schedule(dateTime: LocalDateTime) {
        log("Scheduling: $dateTime")
        scheduler.schedule(dateTime)
    }

    private suspend fun initializeTours(league: League) {
        log("Initializing tours for: $league")
        updateTours(UpdateToursParams(league))
            .onResult { log("Initializing tours result: $it") }
            .onFailure { schedule() }
            .onSuccess { synchronize(league) }
    }

    private suspend fun updatePlayers(tour: Tour) {
        log("Updating players for: ${tour.league}, ${tour.season}")
        val players = playerStorage.getAllPlayers(tour.id).first()
        log("There are: ${players.size} players in the database")
        if (players.isNotEmpty()) {
            return
        }
        updatePlayers(UpdatePlayersParams(tour = tour))
            .onResult { log("Updating players result: $it") }
            .onFailure { error ->
                when (error) {
                    is UpdatePlayersError.Network -> schedule()
                    is UpdatePlayersError.Storage -> when (val insertPlayerError = error.insertPlayerError) {
                        is InsertPlayerError.Errors -> {
                            if (insertPlayerError.teamsNotFound.isNotEmpty()) {
                                updateTeams(tour)
                            }
                        }
                        InsertPlayerError.TourNotFound -> initializeTours(tour.league)
                    }
                }
            }
    }

    private suspend fun updateTeams(tour: Tour) {
        log("Updating teams for: ${tour.league}, ${tour.season}")
        val teams = teamStorage.getAllTeams(tour.id).first()
        log("There are: ${teams.size} teams in the database")
        if (teams.isNotEmpty()) {
            return
        }
        updateTeams(UpdateTeamsParams(tour = tour))
            .onResult { log("Updating teams result: $it") }
            .onFailure { error ->
                when (error) {
                    is UpdateTeamsError.Network -> schedule()
                    is UpdateTeamsError.Storage -> when (error.insertTeamError) {
                        is InsertTeamError.TourTeamAlreadyExists -> { }
                        InsertTeamError.TourNotFound -> initializeTours(tour.league)
                    }
                }
            }
    }

    private fun log(message: String) {
        Logger.i(message = message, tag = tag)
    }
}
