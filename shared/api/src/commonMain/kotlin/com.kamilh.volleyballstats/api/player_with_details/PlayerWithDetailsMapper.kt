package com.kamilh.volleyballstats.api.player_with_details

import com.kamilh.volleyballstats.api.ResponseMapper
import com.kamilh.volleyballstats.api.player_details.PlayerDetailsResponse
import com.kamilh.volleyballstats.api.team_player.TeamPlayerResponse
import com.kamilh.volleyballstats.domain.models.PlayerDetails
import com.kamilh.volleyballstats.domain.models.PlayerWithDetails
import com.kamilh.volleyballstats.domain.models.TeamPlayer
import me.tatarka.inject.annotations.Inject

@Inject
class PlayerWithDetailsMapper(
    private val teamPlayerMapper: ResponseMapper<TeamPlayer, TeamPlayerResponse>,
    private val playerDetailsMapper: ResponseMapper<PlayerDetails, PlayerDetailsResponse>,
) : ResponseMapper<PlayerWithDetails, PlayerWithDetailsResponse> {

    override fun to(from: PlayerWithDetails): PlayerWithDetailsResponse =
        PlayerWithDetailsResponse(
            teamPlayer = teamPlayerMapper.to(from.teamPlayer),
            details = playerDetailsMapper.to(from.details),
        )

    override fun from(from: PlayerWithDetailsResponse): PlayerWithDetails =
        PlayerWithDetails(
            teamPlayer = teamPlayerMapper.from(from.teamPlayer),
            details = playerDetailsMapper.from(from.details),
        )
}