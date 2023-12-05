package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.models.Match
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.TourId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun matchStorageOf(
    insertOrUpdate: InsertMatchesResult = InsertMatchesResult.success(Unit),
    getAllMatches: Flow<List<Match>> = flowOf(emptyList()),
    getMatchIdsWithReport: Flow<List<MatchId>> = flowOf(emptyList()),
    deleteInvalidMatches: (TourId) -> Unit = { },
    deleteAll: (List<MatchId>) -> Unit = { },
): MatchStorage = object : MatchStorage {
    override suspend fun insertOrUpdate(matches: List<Match>, tourId: TourId): InsertMatchesResult = insertOrUpdate
    override suspend fun getAllMatches(tourId: TourId): Flow<List<Match>> = getAllMatches
    override fun getMatchIdsWithReport(tourId: TourId): Flow<List<MatchId>> = getMatchIdsWithReport
    override suspend fun deleteInvalidMatches(tourId: TourId) = deleteInvalidMatches(tourId)
    override suspend fun deleteAll(matchIds: List<MatchId>) = deleteAll(matchIds)
}