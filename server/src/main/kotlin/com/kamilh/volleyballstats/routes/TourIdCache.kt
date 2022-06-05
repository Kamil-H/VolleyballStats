package com.kamilh.volleyballstats.routes

import com.kamilh.volleyballstats.api.ApiConstants
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.domain.models.flatMap
import com.kamilh.volleyballstats.storage.TourStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject

interface TourIdCache {

    suspend fun <T> tourIdFrom(tourIdString: String?, action: suspend (TourId) -> CallResult<T>): CallResult<T>
}

@Inject
@Singleton
class TourIdCacheImpl(tourStorage: TourStorage) : TourIdCache {

    private val allToursCache: Flow<List<Tour>> = tourStorage.getAll()

    override suspend fun <T> tourIdFrom(tourIdString: String?, action: suspend (TourId) -> CallResult<T>): CallResult<T> =
        tourIdString.retrieveLongId(queryParamName = ApiConstants.QUERY_PARAM_TOUR_ID) { TourId(it) }.flatMap { tourId ->
            if (allToursCache.first().find { it.id == tourId } == null) {
                CallResult.failure(CallError.wrongTourId(tourId))
            } else {
                CallResult.success<TourId, CallError>(tourId).flatMap { action(it) }
            }
        }
}