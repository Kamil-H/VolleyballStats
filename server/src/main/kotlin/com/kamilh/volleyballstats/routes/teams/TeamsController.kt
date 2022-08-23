package com.kamilh.volleyballstats.routes.teams

import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.api.team.TeamResponse
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.Team
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.routes.CacheableController
import com.kamilh.volleyballstats.routes.CallResult
import com.kamilh.volleyballstats.routes.TourIdCache
import com.kamilh.volleyballstats.storage.TeamStorage
import com.kamilh.volleyballstats.utils.SafeMap
import com.kamilh.volleyballstats.utils.safeMapOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject

interface TeamsController : CacheableController {

    suspend fun getTeams(tourId: String?): CallResult<List<TeamResponse>>
}

@Inject
@Singleton
class TeamsControllerImpl(
    override val scope: CoroutineScope,
    private val tourIdCache: TourIdCache,
    private val teamStorage: TeamStorage,
    private val teamMapper: ResponseMapper<Team, TeamResponse>,
) : TeamsController {

    private val allTeamsCache: SafeMap<TourId, StateFlow<List<Team>>> = safeMapOf()

    override suspend fun getTeams(tourId: String?): CallResult<List<TeamResponse>> =
        tourIdCache.tourIdFrom(tourId) {
            CallResult.success(allTeamsCache.getCachedFlow(it, teamStorage::getAllTeams).value.map(teamMapper::to))
        }
}
