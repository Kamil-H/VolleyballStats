package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.MatchReport
import com.kamilh.volleyballstats.domain.models.TourId

fun matchReportStorageOf(
    insert: (matchReport: MatchReport, tourId: TourId) -> InsertMatchReportResult = { _, _ ->
        InsertMatchReportResult.success(Unit)
    },
    getMatchReport: (MatchId) -> MatchReport? = { null },
): MatchReportStorage = object : MatchReportStorage {
    override suspend fun insert(matchReport: MatchReport, tourId: TourId): InsertMatchReportResult =
        insert(matchReport, tourId)

    override suspend fun getMatchReport(matchId: MatchId): MatchReport? = getMatchReport(matchId)
}
