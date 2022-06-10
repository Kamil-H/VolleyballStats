package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.models.Player
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.TourId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun playerStorageOf(
    insert: (players: List<Player>, tourId: TourId) -> InsertPlayerResult = { _, _ ->
        InsertPlayerResult.success(Unit)
    },
    getAllPlayersByTeam: Flow<List<Player>> = flowOf(emptyList()),
    getAllPlayers: Flow<List<Player>> = flowOf(emptyList()),
): PlayerStorage = object : PlayerStorage {
    override suspend fun insert(players: List<Player>, tourId: TourId): InsertPlayerResult = insert(players, tourId)
    override fun getAllPlayers(teamId: TeamId, tourId: TourId): Flow<List<Player>> = getAllPlayersByTeam
    override fun getAllPlayers(tourId: TourId): Flow<List<Player>> = getAllPlayers
}