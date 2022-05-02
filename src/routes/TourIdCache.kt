package com.kamilh.routes

import com.kamilh.Singleton
import com.kamilh.models.Tour
import com.kamilh.models.TourId
import com.kamilh.models.flatMap
import com.kamilh.storage.TourStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject
import routes.CallError
import routes.CallResult

interface TourIdCache {

    suspend fun <T> tourIdFrom(tourIdString: String?, action: suspend (TourId) -> CallResult<T>): CallResult<T>
}

@Inject
@Singleton
class TourIdCacheImpl(tourStorage: TourStorage) : TourIdCache {

    private val allToursCache: Flow<List<Tour>> = tourStorage.getAll()

    override suspend fun <T> tourIdFrom(tourIdString: String?, action: suspend (TourId) -> CallResult<T>): CallResult<T> =
        tourIdString.retrieveLongId(queryParamName = QUERY_PARAM_NAME) { TourId(it) }.flatMap { tourId ->
            if (allToursCache.first().find { it.id == tourId } == null) {
                CallResult.failure(CallError.wrongTourId(tourId))
            } else {
                CallResult.success<TourId, CallError>(tourId).flatMap { action(it) }
            }
        }

    companion object {
        private const val QUERY_PARAM_NAME = "tourId"
    }
}