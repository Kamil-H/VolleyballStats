package com.kamilh.volleyballstats.clients.data

import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.network.NetworkResult
import com.kamilh.volleyballstats.network.result.networkFailureOf
import com.kamilh.volleyballstats.repository.polishleague.networkErrorOf

fun statsRepositoryOf(
    getPlayers: NetworkResult<List<Player>> = networkFailureOf(networkErrorOf()),
    getMatches: NetworkResult<List<Match>> = networkFailureOf(networkErrorOf()),
    getMatchReport: NetworkResult<MatchReport> = networkFailureOf(networkErrorOf()),
    getTours: NetworkResult<List<Tour>> = networkFailureOf(networkErrorOf()),
    getTeams: NetworkResult<List<Team>> = networkFailureOf(networkErrorOf()),
): StatsRepository = object : StatsRepository {
    override suspend fun getPlayers(tour: Tour): NetworkResult<List<Player>> = getPlayers
    override suspend fun getMatches(tour: Tour): NetworkResult<List<Match>> = getMatches
    override suspend fun getMatchReport(matchId: MatchId): NetworkResult<MatchReport> = getMatchReport
    override suspend fun getTours(): NetworkResult<List<Tour>> = getTours
    override suspend fun getTeams(tour: Tour): NetworkResult<List<Team>> = getTeams
}
