package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.models.League

fun leagueStorageOf(
    insert: InsertLeagueResult = InsertLeagueResult.success(Unit)
): LeagueStorage = object : LeagueStorage {
    override suspend fun insert(league: League): InsertLeagueResult = insert
}