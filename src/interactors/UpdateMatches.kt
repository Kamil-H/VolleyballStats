package com.kamilh.interactors

import com.kamilh.datetime.ZonedDateTime
import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.storage.InsertMatchStatisticsError
import com.kamilh.storage.InsertMatchesError
import com.kamilh.storage.MatchStorage
import com.kamilh.storage.TourStorage
import kotlinx.coroutines.flow.first

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

class UpdateMatchesInteractor(
    appDispatchers: AppDispatchers,
    private val tourStorage: TourStorage,
    private val matchStorage: MatchStorage,
    private val polishLeagueRepository: PolishLeagueRepository,
    private val updateMatchReports: UpdateMatchReports,
) : UpdateMatches(appDispatchers) {

    override suspend fun doWork(params: UpdateMatchesParams): UpdateMatchesResult {
        val tour = params.tour
        val matches = polishLeagueRepository.getAllMatches(tour.season).value
        if (matches != null) {
            if (matches.isEmpty()) {
                return Result.failure(UpdateMatchesError.NoMatchesInTour)
            }
            val insertResult = matchStorage.insertOrUpdate(matches, tour.id)
            when (insertResult.error) {
                InsertMatchesError.TourNotFound -> return Result.failure(UpdateMatchesError.TourNotFound)
                is InsertMatchesError.TryingToInsertFinishedItems, null -> { }
            }
        }
        val allMatches = matchStorage.getAllMatches(tour.id).first()
        val allMatchesFinished = allMatches.all { it is Match.Finished }
        if (allMatches.isNotEmpty() && allMatchesFinished) {
            val lastFinished = allMatches.filterIsInstance<Match.Finished>().maxByOrNull { it.endTime }!!
            finishTour(tour, lastFinished)
            return Result.success(UpdateMatchesSuccess.SeasonCompleted)
        }

        val potentiallyFinished = allMatches.filterIsInstance<Match.PotentiallyFinished>()
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

        val scheduled = allMatches.filterIsInstance<Match.Scheduled>().minByOrNull { it.date }
        return if (scheduled != null) {
            Result.success(UpdateMatchesSuccess.NextMatch(scheduled.date))
        } else {
            Result.success(UpdateMatchesSuccess.NothingToSchedule)
        }
    }

    private suspend fun finishTour(tour: Tour, lastMatch: Match.Finished) {
        tourStorage.update(tour, lastMatch.endTime.toLocalDate())
    }
}