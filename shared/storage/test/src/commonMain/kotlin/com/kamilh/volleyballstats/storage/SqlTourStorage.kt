package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.models.TourId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun tourStorageOf(
    insert: (tour: Tour) -> InsertTourResult = { InsertTourResult.success(Unit) },
    getAll: Flow<List<Tour>> = flowOf(emptyList()),
    getLatestSeason: Flow<Season?> = flowOf(null),
    getAllByLeague: Flow<List<Tour>> = flowOf(emptyList()),
    getByTourId: Flow<Tour?> = flowOf(null),
    onUpdate: (tour: Tour, endTime: LocalDate) -> Unit = { _, _ -> },
): TourStorage = object : TourStorage {
    override suspend fun insert(tour: Tour): InsertTourResult = insert(tour)
    override fun getAll(): Flow<List<Tour>> = getAll
    override fun getLatestSeason(): Flow<Season?> = getLatestSeason
    override suspend fun getAllByLeague(league: League): Flow<List<Tour>> = getAllByLeague
    override suspend fun getByTourId(tourId: TourId): Flow<Tour?> = getByTourId
    override suspend fun update(tour: Tour, endTime: LocalDate) = onUpdate(tour, endTime)
}