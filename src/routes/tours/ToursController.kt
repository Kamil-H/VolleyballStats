package com.kamilh.routes.tours

import com.kamilh.models.Tour
import com.kamilh.models.api.ResponseMapper
import com.kamilh.models.api.tour.TourResponse
import com.kamilh.storage.TourStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import routes.CallResult

interface ToursController {

    suspend fun getTours(): CallResult<List<TourResponse>>
}

class ToursControllerImpl(
    tourStorage: TourStorage,
    private val tourMapper: ResponseMapper<Tour, TourResponse>,
) : ToursController {

    private val getAllCache: Flow<List<Tour>> = tourStorage.getAll()

    override suspend fun getTours(): CallResult<List<TourResponse>> =
        CallResult.success(getAllCache.first().map(tourMapper::to))
}