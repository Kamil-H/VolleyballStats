package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.models.Match
import com.kamilh.volleyballstats.domain.models.TourId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun matchStorageOf(
    insertOrUpdate: InsertMatchesResult = InsertMatchesResult.success(Unit),
    getAllMatches: Flow<List<Match>> = flowOf(emptyList()),
): MatchStorage = object : MatchStorage {
    override suspend fun insertOrUpdate(match: List<Match>, tourId: TourId): InsertMatchesResult = insertOrUpdate
    override suspend fun getAllMatches(tourId: TourId): Flow<List<Match>> = getAllMatches
}