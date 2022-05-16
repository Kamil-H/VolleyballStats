package com.kamilh.volleyballstats.models.api.player_with_details

import com.kamilh.volleyballstats.models.PlayerDetails
import com.kamilh.volleyballstats.models.TeamPlayer
import com.kamilh.volleyballstats.models.api.ResponseMapper
import com.kamilh.volleyballstats.models.api.player_details.PlayerDetailsResponse
import com.kamilh.volleyballstats.models.api.team_player.TeamPlayerResponse
import me.tatarka.inject.annotations.Inject
import com.kamilh.volleyballstats.models.PlayerWithDetails

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