package com.kamilh.volleyballstats.presentation.interactors

import com.kamilh.volleyballstats.clients.data.StatsRepository
import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.interactors.*
import com.kamilh.volleyballstats.storage.MatchStorage
import com.kamilh.volleyballstats.storage.TourStorage
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject

@Inject
class UpdateMatchesInteractor(
    appDispatchers: AppDispatchers,
    private val statsRepository: StatsRepository,
    private val matchStorage: MatchStorage,
    private val tourStorage: TourStorage,
    private val updateMatchReports: UpdateMatchReports,
    private val synchronizeStateSender: SynchronizeStateSender,
) : UpdateMatches(appDispatchers) {

    override suspend fun doWork(params: UpdateMatchesParams): UpdateMatchesResult {
        val tour = params.tour
        return statsRepository.getMatches(tour)
            .mapError { UpdateMatchesError.Network(it) }
            .flatMap { matches ->
                when {
                    matches.isEmpty() -> Result.failure(UpdateMatchesError.NoMatchesInTour)
                    matchStorage.insertOrUpdate(matches, tour.id).error != null -> Result.failure(UpdateMatchesError.TourNotFound)
                    else -> updateMatchReports(tour, matches)
                }
            }
    }

    private suspend fun updateMatchReports(tour: Tour, matches: List<Match>): UpdateMatchesResult {
        val savedMatches = matchStorage.getAllMatches(tour.id).first()
        val allWithReports = savedMatches.all { it.hasReport }
        if (allWithReports) {
            val tourEndDate = tourEndDate(tour)
            if (tourEndDate != null) {
                tourStorage.update(tour, tourEndDate)
                return Result.success(UpdateMatchesSuccess.SeasonCompleted)
            }
        }
        val matchesWithoutReport = savedMatches
            .filterNot { it.hasReport }
            .filter { match -> matches.find { it.id == match.id }?.hasReport == true }
            .map { it.id }
        synchronizeStateSender.send(SynchronizeState.UpdatingMatches(tour = tour, matches = matchesWithoutReport))
        return updateMatchReports(UpdateMatchReportParams(tour, matchesWithoutReport))
            .mapError { it.toUpdateMatchesError() }
            .flatMap { createResult(tour) }
    }

    private fun UpdateMatchReportError.toUpdateMatchesError(): UpdateMatchesError =
        when (this) {
            is UpdateMatchReportError.Insert -> UpdateMatchesError.Insert(error)
            is UpdateMatchReportError.Network -> UpdateMatchesError.Network(networkError)
        }

    private suspend fun createResult(tour: Tour): UpdateMatchesResult {
        val latestMatchDate = latestMatchDate(tour)
        return UpdateMatchesResult.success(
            if (latestMatchDate != null) {
                UpdateMatchesSuccess.NextMatch(latestMatchDate)
            } else {
                UpdateMatchesSuccess.NothingToSchedule
            }
        )
    }

    private suspend fun latestMatchDate(tour: Tour): ZonedDateTime? =
        matchStorage.getAllMatches(tour.id).first()
            .filterNot { it.hasReport }
            .mapNotNull { it.date }
            .minByOrNull { it }

    private suspend fun tourEndDate(tour: Tour): LocalDate? =
        statsRepository.getTours().map { tours -> tours.first { it.id == tour.id }.endDate }.value
}
