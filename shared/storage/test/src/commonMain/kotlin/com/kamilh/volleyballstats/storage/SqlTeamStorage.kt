package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.models.Result
import com.kamilh.volleyballstats.domain.models.Team
import com.kamilh.volleyballstats.domain.models.TourId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun teamStorageOf(
    insert: InsertTeamResult = Result.success(Unit),
    getAllTeams: Flow<List<Team>> = flowOf(emptyList()),
    getTeam: List<Team> = emptyList(),
): TeamStorage =
    object : TeamStorage {
        override suspend fun insert(teams: List<Team>, tourId: TourId): InsertTeamResult = insert
        override suspend fun getAllTeams(tourId: TourId): Flow<List<Team>> = getAllTeams
        override suspend fun getTeam(name: String, code: String, tourId: TourId): Team? = getTeam.firstOrNull { it.name == name }
    }