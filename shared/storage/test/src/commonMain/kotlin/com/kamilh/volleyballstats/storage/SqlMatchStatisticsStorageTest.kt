package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.models.MatchStatistics
import com.kamilh.volleyballstats.domain.models.TourId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun matchStatisticsStorageOf(
    insert: (matchStatistics: MatchStatistics, tourId: TourId) -> InsertMatchStatisticsResult = { _, _ ->
        InsertMatchStatisticsResult.success(Unit)
    },
    getAllMatchStatistics: Flow<List<MatchStatistics>> = flowOf(emptyList()),
): MatchStatisticsStorage = object : MatchStatisticsStorage {
    override suspend fun insert(matchStatistics: MatchStatistics, tourId: TourId): InsertMatchStatisticsResult =
        insert(matchStatistics, tourId)

    override fun getAllMatchStatistics(): Flow<List<MatchStatistics>> = getAllMatchStatistics
}