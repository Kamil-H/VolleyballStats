package com.kamilh.interactors

import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.storage.InsertMatchStatisticsError
import com.kamilh.storage.InsertMatchesError
import com.kamilh.storage.MatchStorage
import com.kamilh.storage.TourStorage
import kotlinx.coroutines.flow.first
import java.time.OffsetDateTime

typealias UpdateMatches = Interactor<UpdateMatchesParams, UpdateMatchesResult>

data class UpdateMatchesParams(val league: League, val tour: TourYear)

typealias UpdateMatchesResult = Result<UpdateMatchesSuccess, UpdateMatchesError>

sealed class UpdateMatchesSuccess {
    object SeasonCompleted : UpdateMatchesSuccess()
    object NothingToSchedule : UpdateMatchesSuccess()
    class NextMatch(val dateTime: OffsetDateTime) : UpdateMatchesSuccess()
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
        val (league, tourYear) = params
        val tour = tourStorage.getByTourYearAndLeague(tourYear, league).first()
            ?: return Result.failure(UpdateMatchesError.TourNotFound)

        val matches = polishLeagueRepository.getAllMatches(params.tour).value
        if (matches != null) {
            if (matches.isEmpty()) {
                return Result.failure(UpdateMatchesError.NoMatchesInTour)
            }
            val insertResult = matchStorage.insertOrUpdate(matches, league, tourYear)
            when (insertResult.error) {
                InsertMatchesError.TourNotFound -> return Result.failure(UpdateMatchesError.TourNotFound)
                is InsertMatchesError.TryingToInsertSavedItems, null -> { }
            }
        }
        val allMatches = matchStorage.getAllMatches(league, tourYear).first()
        val allMatchesSaved = allMatches.all { it is AllMatchesItem.Saved }
        if (allMatchesSaved) {
            val lastSaved = allMatches.filterIsInstance<AllMatchesItem.Saved>().maxByOrNull { it.endTime }!!
            finishTour(tour, lastSaved)
            return Result.success(UpdateMatchesSuccess.SeasonCompleted)
        }

        val potentiallyFinished = allMatches.filterIsInstance<AllMatchesItem.PotentiallyFinished>()
        if (potentiallyFinished.isNotEmpty()) {
            val error = updateMatchReports(UpdateMatchReportParams(league, tourYear, potentiallyFinished)).mapError {
                when (it) {
                    is UpdateMatchReportError.Network -> UpdateMatchesError.Network(it.networkError)
                    is UpdateMatchReportError.Insert -> UpdateMatchesError.Insert(it.error)
                }
            }.error

            if (error != null) {
                return Result.failure(error)
            }
        }

        val scheduled = allMatches.filterIsInstance<AllMatchesItem.Scheduled>().minByOrNull { it.date }
        return if (scheduled != null) {
            Result.success(UpdateMatchesSuccess.NextMatch(scheduled.date))
        } else {
            Result.success(UpdateMatchesSuccess.NothingToSchedule)
        }
    }

    private suspend fun finishTour(tour: Tour, lastMatch: AllMatchesItem.Saved) {
        tourStorage.update(tour, lastMatch.endTime.toLocalDate())
    }
}