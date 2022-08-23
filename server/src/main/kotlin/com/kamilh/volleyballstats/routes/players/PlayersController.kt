package com.kamilh.volleyballstats.routes.players

import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.api.player.PlayerResponse
import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.Player
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.routes.CacheableController
import com.kamilh.volleyballstats.routes.CallResult
import com.kamilh.volleyballstats.routes.TourIdCache
import com.kamilh.volleyballstats.storage.PlayerStorage
import com.kamilh.volleyballstats.utils.SafeMap
import com.kamilh.volleyballstats.utils.safeMapOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject

interface PlayersController : CacheableController {

    suspend fun getPlayersWithDetails(tourId: String?): CallResult<List<PlayerResponse>>
}

@Inject
@Singleton
class PlayersControllerImpl(
    private val tourIdCache: TourIdCache,
    override val scope: CoroutineScope,
    private val playerStorage: PlayerStorage,
    private val playerWithDetailsMapper: ResponseMapper<Player, PlayerResponse>,
) : PlayersController {

    private val playerWithDetailsCache: SafeMap<TourId, StateFlow<List<Player>>> = safeMapOf()

    override suspend fun getPlayersWithDetails(tourId: String?): CallResult<List<PlayerResponse>> =
        tourIdCache.tourIdFrom(tourId) {
            CallResult.success(playerWithDetailsCache.getCachedFlow(it, playerStorage::getAllPlayers).value.map(playerWithDetailsMapper::to))
        }
}
