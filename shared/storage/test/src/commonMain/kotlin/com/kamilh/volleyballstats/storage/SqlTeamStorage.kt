package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.domain.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun teamStorageOf(
    insert: InsertTeamResult = Result.success(Unit),
    getAllTeams: Flow<List<Team>> = flowOf(emptyList()),
    getTeam: List<Team> = emptyList(),
    getTeamSnapshots: Flow<Set<TeamSnapshot>> = flowOf(emptySet()),
): TeamStorage =
    object : TeamStorage {
        override suspend fun insert(teams: List<Team>, tourId: TourId): InsertTeamResult = insert
        override suspend fun getAllTeams(tourId: TourId): Flow<List<Team>> = getAllTeams
        override suspend fun getTeam(name: String, code: String, tourId: TourId): Team? = getTeam.firstOrNull { it.name == name }
        override suspend fun getTeamSnapshots(seasons: List<Season>): Flow<Set<TeamSnapshot>> = getTeamSnapshots
    }