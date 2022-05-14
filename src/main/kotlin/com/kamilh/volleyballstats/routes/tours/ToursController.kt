package com.kamilh.volleyballstats.routes.tours

import com.kamilh.volleyballstats.Singleton
import com.kamilh.volleyballstats.models.Tour
import com.kamilh.volleyballstats.models.api.ResponseMapper
import com.kamilh.volleyballstats.models.api.tour.TourResponse
import com.kamilh.volleyballstats.storage.TourStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject
import com.kamilh.volleyballstats.routes.CallResult

interface ToursController {

    suspend fun getTours(): CallResult<List<TourResponse>>
}

@Inject
@Singleton
class ToursControllerImpl(
    tourStorage: TourStorage,
    private val tourMapper: ResponseMapper<Tour, TourResponse>,
) : ToursController {

    private val getAllCache: Flow<List<Tour>> = tourStorage.getAll()

    override suspend fun getTours(): CallResult<List<TourResponse>> =
        CallResult.success(getAllCache.first().map(tourMapper::to))
}