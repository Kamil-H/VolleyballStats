package com.kamilh.volleyballstats.routes.tours

import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.api.tour.TourResponse
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.routes.CacheableController
import com.kamilh.volleyballstats.routes.CallResult
import com.kamilh.volleyballstats.storage.TourStorage
import com.kamilh.volleyballstats.utils.LazySuspend
import kotlinx.coroutines.CoroutineScope
import me.tatarka.inject.annotations.Inject

interface ToursController : CacheableController {

    suspend fun getTours(): CallResult<List<TourResponse>>
}

@Inject
@Singleton
class ToursControllerImpl(
    tourStorage: TourStorage,
    override val scope: CoroutineScope,
    private val tourMapper: ResponseMapper<Tour, TourResponse>,
) : ToursController {

    private val getAllCache = LazySuspend { tourStorage.getAll().cache() }

    override suspend fun getTours(): CallResult<List<TourResponse>> =
        CallResult.success(getAllCache().value.map(tourMapper::to))
}
