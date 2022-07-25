package com.kamilh.volleyballstats.matchanalyzer.strategies

import com.kamilh.volleyballstats.domain.models.Effect
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.Skill
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.playerIdOf
import com.kamilh.volleyballstats.matchanalyzer.AnalysisInput
import com.kamilh.volleyballstats.models.analysisInputPlayOf

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