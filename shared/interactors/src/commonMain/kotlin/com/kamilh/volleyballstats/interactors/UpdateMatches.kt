package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.interactor.Interactor
import com.kamilh.volleyballstats.domain.models.Error
import com.kamilh.volleyballstats.domain.models.Result
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.network.NetworkError
import com.kamilh.volleyballstats.storage.InsertMatchReportError

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
    class Insert(val error: InsertMatchReportError) : UpdateMatchesError("Insert(error: ${error.message})")
}