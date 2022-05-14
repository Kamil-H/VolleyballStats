package com.kamilh.routes.players

import com.kamilh.Singleton
import com.kamilh.models.PlayerWithDetails
import com.kamilh.models.TourId
import com.kamilh.models.api.ResponseMapper
import com.kamilh.models.api.player_with_details.PlayerWithDetailsResponse
import com.kamilh.routes.TourIdCache
import com.kamilh.storage.PlayerStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject
import com.kamilh.routes.CallResult
import com.kamilh.utils.SafeMap
import com.kamilh.utils.safeMapOf

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