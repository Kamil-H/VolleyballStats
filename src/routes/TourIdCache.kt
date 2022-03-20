package com.kamilh.routes

import com.kamilh.models.Tour
import com.kamilh.models.TourId
import com.kamilh.models.flatMap
import com.kamilh.storage.TourStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import routes.CallError
import routes.CallResult

interface TourIdCache {

    suspend fun <T> tourIdFrom(tourIdString: String?, action: suspend (TourId) -> CallResult<T>): CallResult<T>
}

class TourIdCacheImpl(tourStorage: TourStorage) : TourIdCache {

    private val allToursCache: Flow<List<Tour>> = tourStorage.getAll()

    override suspend fun <T> tourIdFrom(tourIdString: String?, action: suspend (TourId) -> CallResult<T>): CallResult<T> {
        tourIdString ?: return CallResult.failure(CallError.missingParameter(queryParam = QUERY_PARAM_NAME))
        val number = tourIdString.toLongOrNull() ?: return CallResult.failure(
            CallError.wrongParameterType(queryParam = QUERY_PARAM_NAME, correctType = CORRECT_QUERY_PARAM_TYPE)
        )
        val tourId = TourId(number)
        return if (allToursCache.first().find { it.id == tourId } == null) {
            CallResult.failure(CallError.wrongTourId(tourId))
        } else {
            CallResult.success<TourId, CallError>(TourId(number)).flatMap { action(it) }
        }
    }

    companion object {
        private const val QUERY_PARAM_NAME = "tourId"
        private const val CORRECT_QUERY_PARAM_TYPE = "Integer"
    }
}