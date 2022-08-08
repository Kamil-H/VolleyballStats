package com.kamilh.volleyballstats.network.repository

import com.kamilh.volleyballstats.domain.models.Team
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.network.NetworkResult

interface PolishLeagueRepository {

    suspend fun getTours(): NetworkResult<List<Tour>>

    suspend fun getTeams(tour: Tour): NetworkResult<List<Team>>
}
