package models

import com.kamilh.models.Player
import com.kamilh.models.PlayerDetails

data class PlayerWithDetails(
    val player: Player,
    val details: PlayerDetails,
)