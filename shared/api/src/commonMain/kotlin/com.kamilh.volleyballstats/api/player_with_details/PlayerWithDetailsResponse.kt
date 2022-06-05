package com.kamilh.volleyballstats.api.player_with_details

import com.kamilh.volleyballstats.api.player_details.PlayerDetailsResponse
import com.kamilh.volleyballstats.api.team_player.TeamPlayerResponse
import kotlinx.serialization.Serializable

@Serializable
class PlayerWithDetailsResponse(
    val teamPlayer: TeamPlayerResponse,
    val details: PlayerDetailsResponse,
)