package com.kamilh.volleyballstats.routes.players

import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.api.player.PlayerResponse
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.Player
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

    suspend fun getPlayersWithDetails(tourId: String?): CallResult<List<PlayerResponse>>
}

@Inject
@Singleton
class PlayersControllerImpl(
    private val tourIdCache: TourIdCache,
    private val playerStorage: PlayerStorage,
    private val playerWithDetailsMapper: ResponseMapper<Player, PlayerResponse>,
) : PlayersController {

    private val playerWithDetailsCache: SafeMap<TourId, Flow<List<Player>>> = safeMapOf()

    override suspend fun getPlayersWithDetails(tourId: String?): CallResult<List<PlayerResponse>> =
        tourIdCache.tourIdFrom(tourId) {
            CallResult.success(allPlayersFlow(it).first().map(playerWithDetailsMapper::to))
        }

    private suspend fun allPlayersFlow(tourId: TourId): Flow<List<Player>> =
        playerWithDetailsCache.access { map ->
            map[tourId] ?: playerStorage.getAllPlayers(tourId).apply {
                map[tourId] = this
            }
        }
}
