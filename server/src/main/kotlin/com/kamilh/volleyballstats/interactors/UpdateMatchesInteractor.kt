package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.models.Match
import com.kamilh.volleyballstats.domain.models.MatchInfo
import com.kamilh.volleyballstats.domain.models.Result
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.models.error
import com.kamilh.volleyballstats.domain.models.mapError
import com.kamilh.volleyballstats.domain.models.value
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import com.kamilh.volleyballstats.repository.polishleague.PlsRepository
import com.kamilh.volleyballstats.storage.MatchStorage
import com.kamilh.volleyballstats.storage.TourStorage
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration.Companion.days

@Inject
class UpdateMatchesInteractor(
    appDispatchers: AppDispatchers,
    private val tourStorage: TourStorage,
    private val matchStorage: MatchStorage,
    private val polishLeagueRepository: PlsRepository,
    private val updateMatchReports: UpdateMatchReports,
) : UpdateMatches(appDispatchers) {

    @Suppress("ReturnCount", "LongMethod", "ComplexMethod")
    override suspend fun doWork(params: UpdateMatchesParams): UpdateMatchesResult {
        val tour = params.tour

        val matchResult = polishLeagueRepository.getAllMatches(tour.season)
        val matchError = matchResult.error
        if (matchError != null) {
            return Result.failure(
                UpdateMatchesError.UpdateMatchReportError(
                    networkErrors = listOf(matchError),
                    message = matchError.message,
                )
            )
        }

        val matches = matchResult.value!!
        if (matches.isEmpty()) {
            return Result.failure(UpdateMatchesError.NoMatchesInTour)
        }

        val insertResult = matchStorage.insertOrUpdate(matches.map { it.toMatch() }, tour.id)
        val insertError = insertResult.error
        if (insertError != null) {
            return Result.failure(UpdateMatchesError.TourNotFound)
        }

        matchStorage.deleteInvalidMatches(tour.id)
        val savedMatches = matchStorage.getAllMatches(tour.id).first()
        val allMatchesFinished = savedMatches.all { it.hasReport }
        val lastFinished = savedMatches.filter { it.date != null }.maxByOrNull { it.date!! }?.date
        val lastMatchOlderThanOffset = lastFinished?.plus(TOUR_CONSIDERED_FINISHED_OFFSET)?.let { CurrentDate.zonedDateTime > it } ?: false
        if (savedMatches.isNotEmpty() && (allMatchesFinished || lastMatchOlderThanOffset)) {
            finishTour(tour, lastFinished!!)
            return Result.success(UpdateMatchesSuccess.SeasonCompleted)
        }

        val savedMatchWithReportIds = savedMatches.filter { it.hasReport }.map { it.id }
        val potentiallyFinished = matches
            .filterIsInstance<MatchInfo.PotentiallyFinished>()
            .filter { !savedMatchWithReportIds.contains(it.id) }
            .map { it.id }
        if (potentiallyFinished.isNotEmpty()) {
            val error = updateMatchReports(UpdateMatchReportParams(tour, potentiallyFinished)).mapError {
                UpdateMatchesError.UpdateMatchReportError(
                    networkErrors = it.networkErrors,
                    insertErrors = it.insertErrors,
                    message = it.message,
                )
            }.error

            if (error != null) {
                return Result.failure(error)
            }
        }

        val scheduled = matches.filterIsInstance<MatchInfo.Scheduled>().minByOrNull { it.date }
        return if (scheduled != null) {
            Result.success(UpdateMatchesSuccess.NextMatch(scheduled.date))
        } else {
            Result.success(UpdateMatchesSuccess.NothingToSchedule)
        }
    }

    private suspend fun finishTour(tour: Tour, dateTime: ZonedDateTime) {
        tourStorage.update(tour, dateTime.toLocalDate())
    }

    private fun MatchInfo.toMatch(): Match =
        Match(id = id, date = date, home = home, away = away, hasReport = false)

    companion object {
        private val TOUR_CONSIDERED_FINISHED_OFFSET = 14.days
    }
}
