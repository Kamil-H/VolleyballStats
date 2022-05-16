package com.kamilh.volleyballstats.routes.matches

import com.kamilh.volleyballstats.Singleton
import com.kamilh.volleyballstats.models.*
import com.kamilh.volleyballstats.models.api.ResponseMapper
import com.kamilh.volleyballstats.models.api.match.MatchResponse
import com.kamilh.volleyballstats.models.api.match_report.MatchReportResponse
import com.kamilh.volleyballstats.routes.TourIdCache
import com.kamilh.volleyballstats.routes.retrieveLongId
import com.kamilh.volleyballstats.storage.MatchStatisticsStorage
import com.kamilh.volleyballstats.storage.MatchStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject
import com.kamilh.volleyballstats.routes.CallError
import com.kamilh.volleyballstats.routes.CallResult
import com.kamilh.volleyballstats.utils.SafeMap
import com.kamilh.volleyballstats.utils.safeMapOf

interface MatchesController {

    suspend fun getMatches(tourId: String?): CallResult<List<MatchResponse>>

    suspend fun getMatchReport(matchId: String?): CallResult<MatchReportResponse>
}

@Inject
@Singleton
class MatchesControllerImpl(
    matchStatisticsStorage: MatchStatisticsStorage,
    private val tourIdCache: TourIdCache,
    private val matchStorage: MatchStorage,
    private val matchInfoMapper: ResponseMapper<Match, MatchResponse>,
    private val matchReportMapper: ResponseMapper<MatchStatistics, MatchReportResponse>,
) : MatchesController {

    private val allMatchesCache: SafeMap<TourId, Flow<List<Match>>> = safeMapOf()
    private val allMatchReportsCache: Flow<List<MatchStatistics>> = matchStatisticsStorage.getAllMatchStatistics()

    override suspend fun getMatches(tourId: String?): CallResult<List<MatchResponse>> =
        tourIdCache.tourIdFrom(tourId) {
            CallResult.success(allMatchesFlow(it).first().map(matchInfoMapper::to))
        }

    override suspend fun getMatchReport(matchId: String?): CallResult<MatchReportResponse> =
        matchId.retrieveLongId { MatchId(it) }.flatMap { id ->
            val matchReport = allMatchReportsCache.first().firstOrNull { it.matchId == id }
            if (matchReport != null) {
                CallResult.success(matchReportMapper.to(matchReport))
            } else {
                CallResult.failure(CallError.resourceNotFound(subject = "MatchReport", id = id.value.toString()))
            }
        }

    private suspend fun allMatchesFlow(tourId: TourId): Flow<List<Match>> =
        allMatchesCache.access { map ->
            map[tourId] ?: matchStorage.getAllMatches(tourId).apply {
                map[tourId] = this
            }
        }
}