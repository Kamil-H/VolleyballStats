package com.kamilh.volleyballstats.routes

import com.kamilh.volleyballstats.AppModule
import com.kamilh.volleyballstats.api.ApiUrl
import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.api.StatsApi
import com.kamilh.volleyballstats.api.match.MatchResponse
import com.kamilh.volleyballstats.api.matchreport.MatchReportResponse
import com.kamilh.volleyballstats.api.player.PlayerResponse
import com.kamilh.volleyballstats.api.team.TeamResponse
import com.kamilh.volleyballstats.api.tour.TourResponse
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.storage.*
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
abstract class TestComponent(@Component val parent: AppModule) {
    abstract val databaseFactory: DatabaseFactory
    abstract val json: Json
    abstract val leagueStorage: LeagueStorage
    abstract val matchReportStorage: MatchReportStorage
    abstract val matchStorage: MatchStorage
    abstract val playerStorage: PlayerStorage
    abstract val tourStorage: TourStorage
    abstract val teamStorage: TeamStorage

    abstract val matchMapper: ResponseMapper<Match, MatchResponse>
    abstract val matchReportMapper: ResponseMapper<MatchReport, MatchReportResponse>
    abstract val playerMapper: ResponseMapper<Player, PlayerResponse>
    abstract val teamMapper: ResponseMapper<Team, TeamResponse>
    abstract val tourMapper: ResponseMapper<Tour, TourResponse>

    val storages by lazy {
        Storages(
            leagueStorage = leagueStorage,
            matchReportStorage = matchReportStorage,
            matchStorage = matchStorage,
            playerStorage = playerStorage,
            tourStorage = tourStorage,
            teamStorage = teamStorage,
        )
    }

    val mappers by lazy {
        Mappers(
            matchMapper = matchMapper,
            matchReportMapper = matchReportMapper,
            playerMapper = playerMapper,
            teamMapper = teamMapper,
            tourMapper = tourMapper,
        )
    }

    val statsApi: StatsApi
        @Provides get() = StatsApi(apiUrl = ApiUrl.EMPTY)

    class Storages(
        val leagueStorage: LeagueStorage,
        val matchReportStorage: MatchReportStorage,
        val matchStorage: MatchStorage,
        val playerStorage: PlayerStorage,
        val tourStorage: TourStorage,
        val teamStorage: TeamStorage,
    )

    class Mappers(
        val matchMapper: ResponseMapper<Match, MatchResponse>,
        val matchReportMapper: ResponseMapper<MatchReport, MatchReportResponse>,
        val playerMapper: ResponseMapper<Player, PlayerResponse>,
        val teamMapper: ResponseMapper<Team, TeamResponse>,
        val tourMapper: ResponseMapper<Tour, TourResponse>,
    )
}