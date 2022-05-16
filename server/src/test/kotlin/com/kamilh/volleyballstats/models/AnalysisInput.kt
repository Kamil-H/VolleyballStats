package com.kamilh.volleyballstats.models

import com.kamilh.volleyballstats.match_analyzer.AnalysisInput
import java.util.*

fun analysisInputOf(
    plays: List<AnalysisInput.Play> = listOf(),
): AnalysisInput = AnalysisInput(
    plays = plays,
)

fun analysisInputPlayOf(
    id: String = UUID.randomUUID().toString(),
    effect: Effect = Effect.Perfect,
    player: PlayerId = playerIdOf(),
    skill: Skill = Skill.Attack,
    team: TeamId = teamIdOf(),
    position: PlayerPosition = PlayerPosition.P1,
): AnalysisInput.Play = AnalysisInput.Play(
    id = id,
    effect = effect,
    player = player,
    skill = skill,
    team = team,
    position = position,
)