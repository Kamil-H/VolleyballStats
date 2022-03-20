package com.kamilh.routes.matches

import com.kamilh.models.Match
import com.kamilh.models.MatchStatistics
import com.kamilh.models.TourId
import com.kamilh.models.api.ResponseMapper
import com.kamilh.models.api.match.MatchResponse
import com.kamilh.models.api.match_report.MatchReportResponse
import com.kamilh.routes.TourIdCache
import com.kamilh.storage.MatchStatisticsStorage
import com.kamilh.storage.MatchStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import routes.CallResult
import utils.SafeMap
import utils.safeMapOf

interface MatchesController {

    suspend fun getMatches(tourId: String?): CallResult<List<MatchResponse>>

    suspend fun getMatchReport(matchId: String?): CallResult<MatchReportResponse>
}

class MatchesControllerImpl(
    private val tourIdCache: TourIdCache,
    private val matchStorage: MatchStorage,
    private val matchStatisticsStorage: MatchStatisticsStorage,
    private val matchMapper: ResponseMapper<Match, MatchResponse>,
    private val matchReportMapper: ResponseMapper<MatchStatistics, MatchReportResponse>,
) : MatchesController {

    private val allMatchesCache: SafeMap<TourId, Flow<List<Match>>> = safeMapOf()

    override suspend fun getMatches(tourId: String?): CallResult<List<MatchResponse>> =
        tourIdCache.tourIdFrom(tourId) {
            CallResult.success(allMatchesFlow(it).first().map(matchMapper::to))
        }

    override suspend fun getMatchReport(matchId: String?): CallResult<MatchReportResponse> {

        TODO("Not yet implemented")
    }

    private suspend fun allMatchesFlow(tourId: TourId): Flow<List<Match>> =
        allMatchesCache.access { map ->
            map[tourId] ?: matchStorage.getAllMatches(tourId).apply {
                map[tourId] = this
            }
        }
}