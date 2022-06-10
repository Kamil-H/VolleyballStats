package com.kamilh.volleyballstats.routes.matches

import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.api.match.MatchResponse
import com.kamilh.volleyballstats.api.match_report.MatchReportResponse
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.routes.CallError
import com.kamilh.volleyballstats.routes.CallResult
import com.kamilh.volleyballstats.routes.TourIdCache
import com.kamilh.volleyballstats.routes.retrieveLongId
import com.kamilh.volleyballstats.storage.MatchReportStorage
import com.kamilh.volleyballstats.storage.MatchStorage
import com.kamilh.volleyballstats.utils.SafeMap
import com.kamilh.volleyballstats.utils.safeMapOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject

interface MatchesController {

    suspend fun getMatches(tourId: String?): CallResult<List<MatchResponse>>

    suspend fun getMatchReport(matchId: String?): CallResult<MatchReportResponse>
}

@Inject
@Singleton
class MatchesControllerImpl(
    matchReportStorage: MatchReportStorage,
    private val tourIdCache: TourIdCache,
    private val matchStorage: MatchStorage,
    private val matchInfoMapper: ResponseMapper<Match, MatchResponse>,
    private val matchReportMapper: ResponseMapper<MatchReport, MatchReportResponse>,
) : MatchesController {

    private val allMatchesCache: SafeMap<TourId, Flow<List<Match>>> = safeMapOf()
    private val allMatchReportsCache: Flow<List<MatchReport>> = matchReportStorage.getAllMatchReports()

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