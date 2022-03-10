package com.kamilh.interactors

import com.kamilh.models.*
import com.kamilh.storage.*
import com.kamilh.utils.CurrentDate
import kotlinx.coroutines.flow.first
import utils.Logger
import java.time.LocalDateTime

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
                .onFailure { error ->
                    when (error) {
                        is UpdateMatchesError.Network -> {
                            val networkError = error.networkError
                            if (networkError is NetworkError.UnexpectedException) {
                                networkError.throwable.printStackTrace()
                            }
                            schedule()
                        }
                        UpdateMatchesError.TourNotFound -> initializeTours(tour.league)
                        UpdateMatchesError.NoMatchesInTour -> { }
                        is UpdateMatchesError.Insert -> when (error.error) {
                            InsertMatchStatisticsError.NoPlayersInTeams, is InsertMatchStatisticsError.PlayerNotFound -> updatePlayers(tour)
                            is InsertMatchStatisticsError.TeamNotFound -> updateTeams(tour)
                            InsertMatchStatisticsError.TourNotFound -> initializeTours(league = tour.league)
                        }
                    }
                }
                .onSuccess {
                    when (it) {
                        UpdateMatchesSuccess.NothingToSchedule, UpdateMatchesSuccess.SeasonCompleted -> { }
                        is UpdateMatchesSuccess.NextMatch -> schedule(it.dateTime.toLocalDateTime().plusHours(3))
                    }
                }
        }
    }

    private suspend fun schedule() {
        schedule(CurrentDate.localDateTime.plusHours(1))
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