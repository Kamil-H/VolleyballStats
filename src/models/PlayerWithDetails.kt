package models

import com.kamilh.models.PlayerDetails
import com.kamilh.models.TeamPlayer

data class PlayerWithDetails(
    val teamPlayer: TeamPlayer,
    val details: PlayerDetails,
)