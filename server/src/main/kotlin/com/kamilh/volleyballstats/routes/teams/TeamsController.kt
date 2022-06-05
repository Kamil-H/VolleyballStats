package com.kamilh.volleyballstats.routes.teams

import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.api.team.TeamResponse
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.Team
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.routes.CallResult
import com.kamilh.volleyballstats.routes.TourIdCache
import com.kamilh.volleyballstats.storage.TeamStorage
import com.kamilh.volleyballstats.utils.SafeMap
import com.kamilh.volleyballstats.utils.safeMapOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject

interface TeamsController {

    suspend fun getTeams(tourId: String?): CallResult<List<TeamResponse>>
}

@Inject
@Singleton
class TeamsControllerImpl(
    private val tourIdCache: TourIdCache,
    private val teamStorage: TeamStorage,
    private val teamMapper: ResponseMapper<Team, TeamResponse>,
) : TeamsController {

    private val allTeamsCache: SafeMap<TourId, Flow<List<Team>>> = safeMapOf()

    override suspend fun getTeams(tourId: String?): CallResult<List<TeamResponse>> =
        tourIdCache.tourIdFrom(tourId) {
            CallResult.success(allTeamsFlow(it).first().map(teamMapper::to))
        }

    private suspend fun allTeamsFlow(tourId: TourId): Flow<List<Team>> =
        allTeamsCache.access { map ->
            map[tourId] ?: teamStorage.getAllTeams(tourId).apply {
                map[tourId] = this
            }
        }
}

