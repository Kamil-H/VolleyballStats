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

class UpdateMatchReportError(
    val networkErrors: List<NetworkError> = emptyList(),
    val insertErrors: List<InsertMatchReportError> = emptyList(),
) : Error {

    constructor(error: InsertMatchReportError) : this(insertErrors = listOf(error))

    constructor(error: NetworkError) : this(networkErrors = listOf(error))

    override val message: String
        get() = buildString {
            if (networkErrors.isNotEmpty()) {
                append("Network errors (${networkErrors.size}): ${networkErrors.joinToString { it.message }}")
            }
            if (insertErrors.isNotEmpty()) {
                append("Insert errors (${insertErrors.size}): ${insertErrors.joinToString { it.message }}")
            }
        }
}
