package com.kamilh.volleyballstats.network.repository

import com.kamilh.volleyballstats.domain.models.Team
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.network.NetworkResult
import com.kamilh.volleyballstats.network.result.networkFailureOf
import com.kamilh.volleyballstats.repository.polishleague.networkErrorOf

fun polishLeagueRepositoryOf(
    getTours: NetworkResult<List<Tour>> = networkFailureOf(networkErrorOf()),
    getTeams: NetworkResult<List<Team>> = networkFailureOf(networkErrorOf()),
): PolishLeagueRepository = object : PolishLeagueRepository {
    override suspend fun getTours(): NetworkResult<List<Tour>> = getTours

    override suspend fun getTeams(tour: Tour): NetworkResult<List<Team>> = getTeams
}