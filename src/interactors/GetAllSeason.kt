package com.kamilh.interactors

import com.kamilh.match_analyzer.MatchReportAnalyzer
import com.kamilh.models.*
import com.kamilh.repository.polishleague.PolishLeagueRepository
import com.kamilh.storage.TeamStorage

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
    private val teamStorage: TeamStorage,
    private val matchReportAnalyzer: MatchReportAnalyzer,
): GetAllSeason(appDispatchers) {

    private val list = mutableListOf<Long>()

    override suspend fun doWork(params: GetAllSeasonParams): NetworkResult<Season> {
        polishLeagueRepository.getAllPlayers(params.tour)
            .onSuccess {
                it.forEach {
//                    println("[${it.id}, ${it.position}]")
                }
            }
            .onFailure {  }
//        val teams = polishLeagueRepository.getAllTeams(params.tour).value
//
//        if (teams != null) {
//            teamStorage.insert(params.tour, teams)
//        }
//
//        polishLeagueRepository.getAllPlayers(params.tour)
//            .onSuccess {
//                it.forEach {
//                    println("Player: ${it.name}")
//                }
//            }
//            .onFailure {
//                println("Error getAllPlayers: $it")
//            }

//        getMatchReport(matchReportId = MatchReportId(2103714), params.tour, teams)

//        polishLeagueRepository.getAllMatches(params.tour)
//            .onSuccess {
//                it.filterIsInstance<AllMatchesItem.PotentiallyFinished>()
//                    .forEach { getMatchReport(it.id, params.tour, teams) }
//            }
//            .onFailure {
//                println("Error getAllMatches: $it")
//            }
//
//        val byTeams = actions.groupBy { it.generalInfo.playerInfo.teamId }
//        byTeams.forEach { entry ->
//            val playActions = entry.value
//            val attacks = playActions.filterIsInstance<PlayAction.Attack>()
//            val blocks = playActions.filterIsInstance<PlayAction.Block>()
//            val receives = playActions.filterIsInstance<PlayAction.Receive>()
//            val serves = playActions.filterIsInstance<PlayAction.Serve>()
//            val digs = playActions.filterIsInstance<PlayAction.Dig>()
//
//            println(teams?.first { it.id == entry.key }?.name)
//            println("${serves.perfect().size},${serves.size},${receives.perfect().size},${receives.size},${attacks.perfect().size},${attacks.size},${blocks.perfect().size},${digs.perfect().size}")
//
//            println()
//        }
//
//        actions.filterIsInstance<PlayAction.Block>().groupBy { it.generalInfo.playerInfo.position }.forEach {
//            println("${it.key}, ${it.value.size}")
//        }
//
//        println(list.joinToString { it.toString() })
//        println(list.size)

        return Result.success(Season(emptyList(), emptyList(), emptyList()))
    }

    private fun List<PlayAction>.perfect(): List<PlayAction> = filter { it.generalInfo.effect == Effect.Perfect }

    private suspend fun getMatchReport(matchId: MatchId, tour: Tour, teams: List<Team>?) {
        polishLeagueRepository.getMatchReportId(matchId)
            .onSuccess { matchReportId ->
                getMatchReport(matchReportId, tour, teams)
            }
            .onFailure {
                println("Error getMatchReportId: $it, matchId: ${matchId.value}")
            }
    }

    private suspend fun getMatchReport(matchReportId: MatchReportId, tour: Tour, teams: List<Team>?) {
        polishLeagueRepository.getMatchReport(matchReportId, tour)
            .onSuccess { matchReport ->
                println("${matchReport.matchTeams.home.name} vs ${matchReport.matchTeams.away.name} || ${matchReport.startDate} || ${matchReport.matchId}")
                val stats = matchReportAnalyzer.analyze(matchReport, tour)
                actions.addAll(stats.sets.flatMap { it.points }.flatMap { it.playActions })
            }
            .onFailure {
                list.add(matchReportId.value)
            }
    }

    private val actions = mutableListOf<PlayAction>()
    private suspend fun displayStats(matchReport: MatchReport, tour: Tour, teams: List<Team>?) {
        println("${matchReport.matchTeams.home.name} vs ${matchReport.matchTeams.away.name} || ${matchReport.startDate} || ${matchReport.matchId}")

        val stats = matchReportAnalyzer.analyze(matchReport, tour)
        actions.addAll(stats.sets.flatMap { it.points }.flatMap { it.playActions })
        val playActions = stats.sets
            .flatMap { it.points }
            .flatMap { it.playActions }

        val attacks = playActions.filterIsInstance<PlayAction.Attack>().groupBy { it.generalInfo.playerInfo.teamId }
        val blocks = playActions.filterIsInstance<PlayAction.Block>().groupBy { it.generalInfo.playerInfo.teamId }
        val receives = playActions.filterIsInstance<PlayAction.Receive>().groupBy { it.generalInfo.playerInfo.teamId }
        val serves = playActions.filterIsInstance<PlayAction.Serve>().groupBy { it.generalInfo.playerInfo.teamId }

        print(tag = "attacks", attacks, teams)
        print(tag = "blocks", blocks, teams)
        print(tag = "receives", receives, teams)
        print(tag = "serves", serves, teams)

        println()
        println()
        println()
    }

    private fun print(tag: String, byTeam: Map<TeamId, List<PlayAction>>, teams: List<Team>?) {
        println(tag)
        byTeam.forEach { entry ->
            val all = entry.value.size
            val perfects = entry.value.filter { it.generalInfo.effect == Effect.Perfect }.size
            println("${teams?.first { it.id == entry.key }?.name}, $perfects/$all = ${perfects.toDouble()/all.toDouble()*100.0}")
        }
        println()
    }
}