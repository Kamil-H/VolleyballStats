package com.kamilh.interactors

import com.kamilh.datetime.ZonedDateTime
import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.storage.InsertMatchStatisticsError
import com.kamilh.storage.MatchStorage
import com.kamilh.storage.TourStorage
import com.kamilh.utils.CurrentDate
import kotlinx.coroutines.flow.first
import kotlin.time.Duration.Companion.days
import me.tatarka.inject.annotations.Inject

typealias UpdateMatches = Interactor<UpdateMatchesParams, UpdateMatchesResult>

data class UpdateMatchesParams(val tour: Tour)

typealias UpdateMatchesResult = Result<UpdateMatchesSuccess, UpdateMatchesError>

sealed class UpdateMatchesSuccess {
    object SeasonCompleted : UpdateMatchesSuccess()
    object NothingToSchedule : UpdateMatchesSuccess()
    class NextMatch(val dateTime: ZonedDateTime) : UpdateMatchesSuccess()
}

sealed class UpdateMatchesError(override val message: String) : Error {
    object TourNotFound : UpdateMatchesError("TourNotFound")
    object NoMatchesInTour : UpdateMatchesError("NoMatchesInTour")
    class Network(val networkError: NetworkError) : UpdateMatchesError("Network(networkError: ${networkError.message})")
    class Insert(val error: InsertMatchStatisticsError) : UpdateMatchesError("Insert(error: ${error.message})")
}

@Inject
class UpdateMatchesInteractor(
    appDispatchers: AppDispatchers,
    private val tourStorage: TourStorage,
    private val matchStorage: MatchStorage,
    private val polishLeagueRepository: PolishLeagueRepository,
    private val updateMatchReports: UpdateMatchReports,
) : UpdateMatches(appDispatchers) {

    override suspend fun doWork(params: UpdateMatchesParams): UpdateMatchesResult {
        val tour = params.tour

        val matchResult = polishLeagueRepository.getAllMatches(tour.season)
        val matchError = matchResult.error
        if (matchError != null) {
            return Result.failure(UpdateMatchesError.Network(matchError))
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

        val savedMatches = matchStorage.getAllMatches(tour.id).first()
        val allMatchesFinished = savedMatches.all { it.hasReport }
        val lastFinished = savedMatches.filter { it.date != null }.maxByOrNull { it.date!! }?.date
        val lastMatchOlderThanOffset = lastFinished?.plus(TOUR_CONSIDERED_FINISHED_OFFSET)?.let { CurrentDate.zonedDateTime > it } ?: false
        if (savedMatches.isNotEmpty() && allMatchesFinished && lastMatchOlderThanOffset) {
            finishTour(tour, lastFinished!!)
            return Result.success(UpdateMatchesSuccess.SeasonCompleted)
        }

        val savedMatchWithReportIds = savedMatches.filter { it.hasReport }.map { it.id }
        val potentiallyFinished = matches
            .filterIsInstance<MatchInfo.PotentiallyFinished>()
            .filter { !savedMatchWithReportIds.contains(it.id) }
        if (potentiallyFinished.isNotEmpty()) {
            val error = updateMatchReports(UpdateMatchReportParams(tour, potentiallyFinished)).mapError {
                when (it) {
                    is UpdateMatchReportError.Network -> UpdateMatchesError.Network(it.networkError)
                    is UpdateMatchReportError.Insert -> UpdateMatchesError.Insert(it.error)
                }
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