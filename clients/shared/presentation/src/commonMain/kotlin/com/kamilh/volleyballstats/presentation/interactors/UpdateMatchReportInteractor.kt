package com.kamilh.volleyballstats.presentation.interactors

import com.kamilh.volleyballstats.clients.data.StatsRepository
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.interactors.*
import com.kamilh.volleyballstats.storage.InsertMatchReportError
import com.kamilh.volleyballstats.storage.InsertMatchReportResult
import com.kamilh.volleyballstats.storage.MatchReportStorage
import me.tatarka.inject.annotations.Inject

@Inject
class UpdateMatchReportInteractor(
    appDispatchers: AppDispatchers,
    private val statsRepository: StatsRepository,
    private val matchReportStorage: MatchReportStorage,
    private val updateTeams: UpdateTeams,
    private val updatePlayers: UpdatePlayers,
): UpdateMatchReports(appDispatchers) {

    override suspend fun doWork(params: UpdateMatchReportParams): UpdateMatchReportResult =
        params.matches.map { match ->
            getMatchReport(params.tour, match, shouldTryToFix = true)
        }.toResults().toResult() ?: UpdateMatchReportResult.success(Unit)

    private suspend fun getMatchReport(tour: Tour, matchId: MatchId, shouldTryToFix: Boolean): UpdateMatchReportResult =
        statsRepository.getMatchReport(matchId)
            .mapError(UpdateMatchReportError::Network)
            .flatMap { insert(it, tour, shouldTryToFix = shouldTryToFix) }

    private suspend fun insert(matchReport: MatchReport, tour: Tour, shouldTryToFix: Boolean): UpdateMatchReportResult =
        matchReportStorage.insert(matchReport, tour.id)
            .retryIfNeeded(matchReport, tour, shouldTryToFix = shouldTryToFix)

    private suspend fun InsertMatchReportResult.retryIfNeeded(
        matchReport: MatchReport,
        tour: Tour,
        shouldTryToFix: Boolean,
    ): UpdateMatchReportResult =
        flatMapError {
            handleInsertError(
                matchReport = matchReport,
                tour = tour,
                shouldTryToFix = shouldTryToFix,
                error = it,
            )
        }

    private suspend fun handleInsertError(
        matchReport: MatchReport,
        tour: Tour,
        shouldTryToFix: Boolean,
        error: InsertMatchReportError,
    ): UpdateMatchReportResult =
        when (error) {
            InsertMatchReportError.TourNotFound ->
                UpdateMatchReportResult.failure(UpdateMatchReportError.Insert(error))
            is InsertMatchReportError.PlayerNotFound, InsertMatchReportError.NoPlayersInTeams -> if (shouldTryToFix) {
                updatePlayersOnError(matchReport, tour, originalError = error)
            } else {
                UpdateMatchReportResult.failure(UpdateMatchReportError.Insert(error))
            }
            is InsertMatchReportError.TeamNotFound -> if (shouldTryToFix) {
                updateTeamsOnError(matchReport, tour, originalError = error)
            } else {
                UpdateMatchReportResult.failure(UpdateMatchReportError.Insert(error))
            }
        }

    private suspend fun updatePlayersOnError(
        matchReport: MatchReport,
        tour: Tour,
        originalError: InsertMatchReportError,
    ): UpdateMatchReportResult =
        updatePlayers(UpdatePlayersParams(tour))
            .flatMapError {
                val error = when (it) {
                    is UpdatePlayersError.Network -> UpdateMatchReportError.Network(it.networkError)
                    is UpdatePlayersError.Storage -> UpdateMatchReportError.Insert(originalError)
                }
                UpdateMatchReportResult.failure(error)
            }
            .flatMap {
                insert(matchReport, tour, shouldTryToFix = false)
            }

    private suspend fun updateTeamsOnError(
        matchReport: MatchReport,
        tour: Tour,
        originalError: InsertMatchReportError,
    ): UpdateMatchReportResult =
        updateTeams(UpdateTeamsParams(tour))
            .flatMapError {
                val error = when (it) {
                    is UpdateTeamsError.Network -> UpdateMatchReportError.Network(it.networkError)
                    is UpdateTeamsError.Storage -> UpdateMatchReportError.Insert(originalError)
                }
                UpdateMatchReportResult.failure(error)
            }
            .flatMap {
                insert(matchReport, tour, shouldTryToFix = false)
            }
}
