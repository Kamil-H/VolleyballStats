package com.kamilh.storage

import com.kamilh.models.Team
import com.kamilh.models.Tour

interface TeamStorage {

    fun save(tour: Tour, teams: List<Team>)

    fun getAllTeams(tour: Tour): List<Team>

    fun getTeam(name: String, tour: Tour): Team?
}

class DatabaseTeamStorage : TeamStorage {

    private var teams: List<Team>? = null

    override fun save(tour: Tour, teams: List<Team>) {
        this.teams = teams
    }

    override fun getAllTeams(tour: Tour): List<Team> {
        return teams ?: emptyList()
    }

    override fun getTeam(name: String, tour: Tour): Team? {
        return teams?.firstOrNull { it.name == name }
    }
}