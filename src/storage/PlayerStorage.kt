package com.kamilh.storage

import com.kamilh.models.Team
import com.kamilh.models.Tour

interface PlayerStorage {

    suspend fun insert(tour: Tour, teams: List<Team>)

    suspend fun getAllTeams(tour: Tour): List<Team>

    suspend fun getTeam(name: String, tour: Tour): Team?
}

class SqlPlayerStorage {
}