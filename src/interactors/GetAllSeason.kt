package com.kamilh.interactors

import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

typealias GetAllSeason = Interactor<GetAllSeasonParams, NetworkResult<Season>>

data class GetAllSeasonParams(
    val tour: Tour,
)

data class Season(
    val teams: List<Team>,
    val players: List<Player>,
    val matchReports: List<MatchReport>,
)

class GetAllSeasonInteractor(
    appDispatchers: AppDispatchers,
    private val polishLeagueRepository: PolishLeagueRepository,
): GetAllSeason(appDispatchers) {

    private val list = mutableListOf<Long>()

    override suspend fun doWork(params: GetAllSeasonParams): NetworkResult<Season> {
        polishLeagueRepository.getAllTeams(params.tour)
            .onSuccess {
                it.forEach {
                    println("Team: ${it.name}")
                }
            }
            .onFailure {
                println("Error getAllTeams: $it")
            }

        polishLeagueRepository.getAllPlayers(params.tour)
            .onSuccess {
                it.forEach {
                    println("Player: ${it.name}")
                }
            }
            .onFailure {
                println("Error getAllPlayers: $it")
            }

        polishLeagueRepository.getAllMatches(params.tour)
            .onSuccess {
                coroutineScope {
                    it.filterIsInstance<AllMatchesItem.PotentiallyFinished>()
                        .map { async { getMatchReport(it.id, params.tour) } }
                        .awaitAll()
                }
            }
            .onFailure {
                println("Error getAllMatches: $it")
            }

        println(list.joinToString { it.toString() })
        println(list.size)

        return Result.success(Season(emptyList(), emptyList(), emptyList()))
    }

    private suspend fun getMatchReport(matchId: MatchId, tour: Tour) {
        polishLeagueRepository.getMatchReportId(matchId)
            .onSuccess { matchReportId ->
                polishLeagueRepository.getMatchReport(matchReportId, tour)
                    .onSuccess { matchReport ->
                        println("Match: ${matchReport.teams.home.name} vs ${matchReport.teams.away.name}")
                    }
                    .onFailure {
                        list.add(matchReportId.value)
                    }
            }
            .onFailure {
                println("Error getMatchReportId: $it, matchId: ${matchId.value}")
            }
    }
}