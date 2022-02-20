package com.kamilh.interactors

import com.kamilh.match_analyzer.MatchReportAnalyzerError
import com.kamilh.models.*
import com.kamilh.storage.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import utils.Logger
import java.time.LocalDateTime

class Synchronizer(
    private val applicationScope: CoroutineScope,
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
        Logger.i("Synchronizing: $league", tag)
        val tours = tourStorage.getAllByLeague(League.POLISH_LEAGUE).first()
        if (tours.isEmpty()) {
            Logger.i("Tours are empty", tag)
            initializeTours(league)
        } else {
            updateMatches(tours.filterNot { it.isFinished })
        }
    }

    private suspend fun updateMatches(tours: List<Tour>) {
        tours.forEach { tour ->
            log("Updating matches ${tour.league}, ${tour.year}")
            updateTeams(tour.league, tour.year)
            updatePlayers(tour.league, tour.year)
            updateMatches(UpdateMatchesParams(league = tour.league, tour = tour.year))
                .onResult { log("Updating matches result: $it") }
                .onFailure { error ->
                    when (error) {
                        is UpdateMatchesError.Network -> schedule()
                        UpdateMatchesError.TourNotFound -> initializeTours(tour.league)
                        UpdateMatchesError.NoMatchesInTour -> { }
                        is UpdateMatchesError.Analyze -> when (error.error) {
                            is MatchReportAnalyzerError.TeamNotFound -> updateTeams(tour.league, tour.year)
                            is MatchReportAnalyzerError.WrongSetsCount -> { }
                        }
                        is UpdateMatchesError.Insert -> when (error.error) {
                            InsertMatchStatisticsError.NoPlayersInTeams, is InsertMatchStatisticsError.PlayerNotFound ->
                                updatePlayers(tour.league, tour.year)
                            is InsertMatchStatisticsError.TeamNotFound -> updateTeams(tour.league, tour.year)
                            InsertMatchStatisticsError.TourNotFound -> initializeTours(league = tour.league)
                        }
                    }
                }
        }
    }

    private suspend fun schedule() {
        schedule(LocalDateTime.now().plusHours(1))
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

    private suspend fun updatePlayers(league: League, tourYear: TourYear) {
        log("Updating players for: $league, $tourYear")
        val players = playerStorage.getAllPlayers(league, tourYear).first()
        log("There are: ${players.size} players in the database")
        if (players.isNotEmpty()) {
            return
        }
        updatePlayers(UpdatePlayersParams(league = league, tour = tourYear))
            .onResult { log("Updating players result: $it") }
            .onFailure { error ->
                when (error) {
                    is UpdatePlayersError.Network -> schedule()
                    is UpdatePlayersError.Storage -> when (val insertPlayerError = error.insertPlayerError) {
                        is InsertPlayerError.Errors -> {
                            if (insertPlayerError.teamsNotFound.isNotEmpty()) {
                                updateTeams(league, tourYear)
                            }
                        }
                        InsertPlayerError.TourNotFound -> initializeTours(league)
                    }
                }
            }
    }

    private suspend fun updateTeams(league: League, tourYear: TourYear) {
        log("Updating teams for: $league, $tourYear")
        val teams = teamStorage.getAllTeams(league, tourYear).first()
        log("There are: ${teams.size} teams in the database")
        if (teams.isNotEmpty()) {
            return
        }
        updateTeams(UpdateTeamsParams(league = league, tour = tourYear))
            .onResult { log("Updating teams result: $it") }
            .onFailure { error ->
                when (error) {
                    is UpdateTeamsError.Network -> schedule()
                    is UpdateTeamsError.Storage -> when (error.insertTeamError) {
                        is InsertTeamError.TourTeamAlreadyExists -> { }
                        InsertTeamError.TourNotFound -> initializeTours(league)
                    }
                }
            }
    }

    private fun log(message: String) {
        Logger.i(message = message, tag = tag)
    }
}

fun interface SynchronizeScheduler {
    suspend fun schedule(dateTime: LocalDateTime)
}