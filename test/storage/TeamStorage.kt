package com.kamilh.storage

import com.kamilh.models.Team
import com.kamilh.models.Tour

fun teamStorageOf(
    getAllTeams: List<Team> = emptyList(),
    getTeam: List<Team> = emptyList(),
): TeamStorage =
    object : TeamStorage {
        override fun save(tour: Tour, teams: List<Team>) {  }
        override fun getAllTeams(tour: Tour): List<Team> = getAllTeams
        override fun getTeam(name: String, tour: Tour): Team? = getTeam.firstOrNull { it.name == name }
    }