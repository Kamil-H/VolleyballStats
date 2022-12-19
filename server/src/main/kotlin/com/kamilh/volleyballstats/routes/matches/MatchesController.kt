package com.kamilh.volleyballstats.routes.matches

import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.api.match.MatchResponse
import com.kamilh.volleyballstats.api.matchreport.MatchReportResponse
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.interactors.GetMatchReport
import com.kamilh.volleyballstats.routes.*
import com.kamilh.volleyballstats.storage.MatchStorage
import com.kamilh.volleyballstats.utils.SafeMap
import com.kamilh.volleyballstats.utils.safeMapOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject

interface MatchesController : CacheableController {

    suspend fun getMatches(tourId: String?): CallResult<List<MatchResponse>>

    suspend fun getMatchReport(matchId: String?): CallResult<MatchReportResponse>
}

@Inject
@Singleton
class MatchesControllerImpl(
    private val getMatchReport: GetMatchReport,
    override val scope: CoroutineScope,
    private val tourIdCache: TourIdCache,
    private val matchStorage: MatchStorage,
    private val matchInfoMapper: ResponseMapper<Match, MatchResponse>,
    private val matchReportMapper: ResponseMapper<MatchReport, MatchReportResponse>,
) : MatchesController {

    private val allMatchesCache: SafeMap<TourId, StateFlow<List<Match>>> = safeMapOf()

    override suspend fun getMatches(tourId: String?): CallResult<List<MatchResponse>> =
        tourIdCache.tourIdFrom(tourId) {
            CallResult.success(allMatchesCache.getCachedFlow(it, matchStorage::getAllMatches).value.map(matchInfoMapper::to))
        }

    override suspend fun getMatchReport(matchId: String?): CallResult<MatchReportResponse> =
        matchId.retrieveLongId { MatchId(it) }.flatMap { id ->
            val matchReport = getMatchReport(id)
            if (matchReport != null) {
                CallResult.success(matchReportMapper.to(matchReport))
            } else {
                CallResult.failure(CallError.resourceNotFound(subject = "MatchReport", id = id.value.toString()))
            }
        }
}
