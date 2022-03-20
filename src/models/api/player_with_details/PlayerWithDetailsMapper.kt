package com.kamilh.models.api.player_with_details

import com.kamilh.models.PlayerDetails
import com.kamilh.models.TeamPlayer
import com.kamilh.models.api.ResponseMapper
import com.kamilh.models.api.player_details.PlayerDetailsResponse
import com.kamilh.models.api.team_player.TeamPlayerResponse
import models.PlayerWithDetails

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