package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.interactor.Interactor
import com.kamilh.volleyballstats.domain.models.Error
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.Result
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.network.NetworkError
import com.kamilh.volleyballstats.storage.InsertMatchReportError

typealias UpdateMatchReports = Interactor<UpdateMatchReportParams, UpdateMatchReportResult>

data class UpdateMatchReportParams(
    val tour: Tour,
    val matches: List<MatchId>,
)

typealias UpdateMatchReportResult = Result<Unit, UpdateMatchReportError>

sealed class UpdateMatchReportError(override val message: String) : Error {
    class Network(val networkError: NetworkError) : UpdateMatchReportError("Network(networkError=${networkError.message}")
    class Insert(val error: InsertMatchReportError) : UpdateMatchReportError("Insert(error=${error.message}")
}
