package com.kamilh.storage

import com.kamilh.models.Team
import com.kamilh.models.Tour

interface TeamStorage {

    fun getAllTeams(tour: Tour): List<Team>
}