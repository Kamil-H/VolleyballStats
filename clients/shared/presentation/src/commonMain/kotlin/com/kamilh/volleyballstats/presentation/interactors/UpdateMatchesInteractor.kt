package com.kamilh.volleyballstats.presentation.interactors

import com.kamilh.volleyballstats.clients.data.StatsRepository
import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.models.Match
import com.kamilh.volleyballstats.domain.models.Result
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.models.error
import com.kamilh.volleyballstats.domain.models.flatMap
import com.kamilh.volleyballstats.domain.models.map
import com.kamilh.volleyballstats.domain.models.mapError
import com.kamilh.volleyballstats.domain.models.value
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import com.kamilh.volleyballstats.interactors.SynchronizeState
import com.kamilh.volleyballstats.interactors.SynchronizeStateSender
import com.kamilh.volleyballstats.interactors.UpdateMatchReportError
import com.kamilh.volleyballstats.interactors.UpdateMatchReportParams
import com.kamilh.volleyballstats.interactors.UpdateMatchReports
import com.kamilh.volleyballstats.interactors.UpdateMatches
import com.kamilh.volleyballstats.interactors.UpdateMatchesError
import com.kamilh.volleyballstats.interactors.UpdateMatchesParams
import com.kamilh.volleyballstats.interactors.UpdateMatchesResult
import com.kamilh.volleyballstats.interactors.UpdateMatchesSuccess
import com.kamilh.volleyballstats.storage.MatchStorage
import com.kamilh.volleyballstats.storage.TourStorage
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration.Companion.days

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
            .mapError(UpdateMatchesError::UpdateMatchReportError)
            .flatMap { matches ->
                when {
                    matches.isEmpty() -> Result.failure(UpdateMatchesError.NoMatchesInTour)
                    matchStorage.insertOrUpdate(matches, tour.id).error != null -> Result.failure(UpdateMatchesError.TourNotFound)
                    else -> updateMatchReports(tour, matches)
                }
            }
    }

    private suspend fun updateMatchReports(tour: Tour, matches: List<Match>): UpdateMatchesResult {
        removeInvalidMatches(tour, matches)
        val savedMatches = matchStorage.getAllMatches(tour.id).first()
        val allWithReports = savedMatches.all { it.hasReport }
        val lastFinished = savedMatches.filter { it.date != null }.maxByOrNull { it.date!! }?.date
        val lastMatchOlderThanOffset = lastFinished?.plus(TOUR_CONSIDERED_FINISHED_OFFSET)?.let { CurrentDate.zonedDateTime > it } ?: false
        if (allWithReports || lastMatchOlderThanOffset) {
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

    private suspend fun removeInvalidMatches(tour: Tour, downloadedMatches: List<Match>) {
        val matchIds = downloadedMatches.map { it.id }
        val savedMatches = matchStorage.getAllMatches(tour.id).first()
        val matchesToDelete = savedMatches.filter { !matchIds.contains(it.id) }
        matchStorage.deleteAll(matchesToDelete.map { it.id })
    }

    private fun UpdateMatchReportError.toUpdateMatchesError(): UpdateMatchesError =
        UpdateMatchesError.UpdateMatchReportError(
            networkErrors = networkErrors,
            insertErrors = insertErrors,
            message = message,
        )

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

    companion object {
        private val TOUR_CONSIDERED_FINISHED_OFFSET = 14.days
    }
}
