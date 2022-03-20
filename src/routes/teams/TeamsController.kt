package com.kamilh.routes.teams

import com.kamilh.models.Team
import com.kamilh.models.TourId
import com.kamilh.models.api.ResponseMapper
import com.kamilh.models.api.team.TeamResponse
import com.kamilh.routes.TourIdCache
import com.kamilh.storage.TeamStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import routes.CallResult
import utils.SafeMap
import utils.safeMapOf

interface TeamsController {

    suspend fun getTeams(tourId: String?): CallResult<List<TeamResponse>>
}

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

