package com.kamilh.match_analyzer.strategies

import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.models.*

data class StrategyTestInput(
    val skill: Skill,
    val team: TeamId,
    val player: PlayerId = playerIdOf(),
    val effect: Effect = Effect.Perfect,
)

fun StrategyTestInput.toPlay(): AnalysisInput.Play =
    analysisInputPlayOf(
        player = player,
        skill = skill,
        team = team,
        effect = effect,
    )