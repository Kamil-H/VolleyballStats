package com.kamilh.volleyballstats.routes.players

import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.api.player_with_details.PlayerWithDetailsResponse
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.PlayerWithDetails
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.routes.CallResult
import com.kamilh.volleyballstats.routes.TourIdCache
import com.kamilh.volleyballstats.storage.PlayerStorage
import com.kamilh.volleyballstats.utils.SafeMap
import com.kamilh.volleyballstats.utils.safeMapOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject

interface PlayersController {

    suspend fun getPlayersWithDetails(tourId: String?): CallResult<List<PlayerWithDetailsResponse>>
}

@Inject
@Singleton
class PlayersControllerImpl(
    private val tourIdCache: TourIdCache,
    private val playerStorage: PlayerStorage,
    private val playerWithDetailsMapper: ResponseMapper<PlayerWithDetails, PlayerWithDetailsResponse>,
) : PlayersController {

    private val playerWithDetailsCache: SafeMap<TourId, Flow<List<PlayerWithDetails>>> = safeMapOf()

    override suspend fun getPlayersWithDetails(tourId: String?): CallResult<List<PlayerWithDetailsResponse>> =
        tourIdCache.tourIdFrom(tourId) {
            CallResult.success(allPlayersFlow(it).first().map(playerWithDetailsMapper::to))
        }

    private suspend fun allPlayersFlow(tourId: TourId): Flow<List<PlayerWithDetails>> =
        playerWithDetailsCache.access { map ->
            map[tourId] ?: playerStorage.getAllPlayers(tourId).apply {
                map[tourId] = this
            }
        }
}