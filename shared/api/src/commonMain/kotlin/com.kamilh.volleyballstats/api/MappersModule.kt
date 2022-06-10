package com.kamilh.volleyballstats.api

import com.kamilh.volleyballstats.api.league.LeagueMapper
import com.kamilh.volleyballstats.api.league.LeagueResponse
import com.kamilh.volleyballstats.api.match.MatchMapper
import com.kamilh.volleyballstats.api.match.MatchResponse
import com.kamilh.volleyballstats.api.match_report.MatchReportMapper
import com.kamilh.volleyballstats.api.match_report.MatchReportResponse
import com.kamilh.volleyballstats.api.player.PlayerMapper
import com.kamilh.volleyballstats.api.player.PlayerResponse
import com.kamilh.volleyballstats.api.team.TeamMapper
import com.kamilh.volleyballstats.api.team.TeamResponse
import com.kamilh.volleyballstats.api.tour.TourMapper
import com.kamilh.volleyballstats.api.tour.TourResponse
import com.kamilh.volleyballstats.domain.models.*
import me.tatarka.inject.annotations.Provides

interface MappersModule {

    val LeagueMapper.bind: ResponseMapper<League, LeagueResponse>
        @Provides get() = this

    val MatchMapper.bind: ResponseMapper<Match, MatchResponse>
        @Provides get() = this

    val MatchReportMapper.bind: ResponseMapper<MatchReport, MatchReportResponse>
        @Provides get() = this

    val PlayerMapper.bind: ResponseMapper<Player, PlayerResponse>
        @Provides get() = this

    val TeamMapper.bind: ResponseMapper<Team, TeamResponse>
        @Provides get() = this

    val TourMapper.bind: ResponseMapper<Tour, TourResponse>
        @Provides get() = this
}