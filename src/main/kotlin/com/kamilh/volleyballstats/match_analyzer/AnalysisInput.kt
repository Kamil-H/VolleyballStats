package com.kamilh.volleyballstats.match_analyzer

import com.kamilh.volleyballstats.models.*

data class AnalysisInput(
    val plays: List<Play>,
) {

    data class Play(
        val id: String,
        val effect: Effect,
        val player: PlayerId,
        val skill: Skill,
        val team: TeamId,
        val position: PlayerPosition?,
    )
}

fun AnalysisInput.generalInfo(play: AnalysisInput.Play): PlayAction.GeneralInfo =
    PlayAction.GeneralInfo(
        playerInfo = PlayAction.PlayerInfo(
            playerId = play.player,
            position = play.position,
            teamId = play.team,
        ),
        effect = play.effect,
        breakPoint = plays.first().team == play.team,
    )