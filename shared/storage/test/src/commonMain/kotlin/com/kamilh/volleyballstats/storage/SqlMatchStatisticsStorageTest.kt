package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.models.MatchReport
import com.kamilh.volleyballstats.domain.models.TourId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun matchStatisticsStorageOf(
    insert: (matchReport: MatchReport, tourId: TourId) -> InsertMatchReportResult = { _, _ ->
        InsertMatchReportResult.success(Unit)
    },
    getAllMatchReport: Flow<List<MatchReport>> = flowOf(emptyList()),
): MatchReportStorage = object : MatchReportStorage {
    override suspend fun insert(matchReport: MatchReport, tourId: TourId): InsertMatchReportResult =
        insert(matchReport, tourId)

    override fun getAllMatchReports(): Flow<List<MatchReport>> = getAllMatchReport
}