package com.kamilh.volleyballstats.models.api

import com.kamilh.volleyballstats.models.*
import com.kamilh.volleyballstats.models.api.league.LeagueMapper
import com.kamilh.volleyballstats.models.api.league.LeagueResponse
import com.kamilh.volleyballstats.models.api.match.MatchMapper
import com.kamilh.volleyballstats.models.api.match.MatchResponse
import com.kamilh.volleyballstats.models.api.match_report.MatchReportMapper
import com.kamilh.volleyballstats.models.api.match_report.MatchReportResponse
import com.kamilh.volleyballstats.models.api.player_details.PlayerDetailsMapper
import com.kamilh.volleyballstats.models.api.player_details.PlayerDetailsResponse
import com.kamilh.volleyballstats.models.api.player_with_details.PlayerWithDetailsMapper
import com.kamilh.volleyballstats.models.api.player_with_details.PlayerWithDetailsResponse
import com.kamilh.volleyballstats.models.api.team.TeamMapper
import com.kamilh.volleyballstats.models.api.team.TeamResponse
import com.kamilh.volleyballstats.models.api.team_player.TeamPlayerMapper
import com.kamilh.volleyballstats.models.api.team_player.TeamPlayerResponse
import com.kamilh.volleyballstats.models.api.tour.TourMapper
import com.kamilh.volleyballstats.models.api.tour.TourResponse
import me.tatarka.inject.annotations.Provides

interface MappersModule {

    val LeagueMapper.bind: ResponseMapper<League, LeagueResponse>
        @Provides get() = this

    val MatchMapper.bind: ResponseMapper<Match, MatchResponse>
        @Provides get() = this

    val MatchReportMapper.bind: ResponseMapper<MatchStatistics, MatchReportResponse>
        @Provides get() = this

    val PlayerDetailsMapper.bind: ResponseMapper<PlayerDetails, PlayerDetailsResponse>
        @Provides get() = this

    val PlayerWithDetailsMapper.bind: ResponseMapper<PlayerWithDetails, PlayerWithDetailsResponse>
        @Provides get() = this

    val TeamMapper.bind: ResponseMapper<Team, TeamResponse>
        @Provides get() = this

    val TeamPlayerMapper.bind: ResponseMapper<TeamPlayer, TeamPlayerResponse>
        @Provides get() = this

    val TourMapper.bind: ResponseMapper<Tour, TourResponse>
        @Provides get() = this
}