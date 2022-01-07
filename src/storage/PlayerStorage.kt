package com.kamilh.storage

import com.kamilh.models.Team
import com.kamilh.models.TourYear

interface PlayerStorage {

    suspend fun insert(tour: TourYear, teams: List<Team>)

    suspend fun getAllTeams(tour: TourYear): List<Team>

    suspend fun getTeam(name: String, tour: TourYear): Team?
}

class SqlPlayerStorage {
}