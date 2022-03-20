package com.kamilh.models.api.player_with_details

import com.kamilh.models.api.player_details.PlayerDetailsResponse
import com.kamilh.models.api.team_player.TeamPlayerResponse
import kotlinx.serialization.Serializable

@Serializable
class PlayerWithDetailsResponse(
    val teamPlayer: TeamPlayerResponse,
    val details: PlayerDetailsResponse,
)