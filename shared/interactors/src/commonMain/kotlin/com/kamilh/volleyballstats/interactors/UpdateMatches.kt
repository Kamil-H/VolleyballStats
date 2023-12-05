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
    data object SeasonCompleted : UpdateMatchesSuccess()
    data object NothingToSchedule : UpdateMatchesSuccess()
    class NextMatch(val dateTime: ZonedDateTime) : UpdateMatchesSuccess()
}

sealed class UpdateMatchesError(override val message: String) : Error {
    data object TourNotFound : UpdateMatchesError("TourNotFound")
    data object NoMatchesInTour : UpdateMatchesError("NoMatchesInTour")
    class UpdateMatchReportError(
        val networkErrors: List<NetworkError> = emptyList(),
        val insertErrors: List<InsertMatchReportError> = emptyList(),
        override val message: String,
    ) : UpdateMatchesError(message) {

        constructor(error: InsertMatchReportError) : this(insertErrors = listOf(error), message = error.message)

        constructor(error: NetworkError) : this(networkErrors = listOf(error), message = error.message)
    }
}
