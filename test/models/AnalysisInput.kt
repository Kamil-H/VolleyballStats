package com.kamilh.models

import com.kamilh.match_analyzer.AnalysisInput
import java.time.LocalDateTime
import java.util.*

fun analysisInputOf(
    plays: List<AnalysisInput.Play> = listOf(),
    matchId: MatchReportId = matchReportIdOf(),
    set: Int = 0,
    currentScore: CurrentScore = currentScoreOf(),
    rallyStartTime: LocalDateTime = LocalDateTime.now(),
    rallyEndTime: LocalDateTime = LocalDateTime.now(),
): AnalysisInput = AnalysisInput(
    plays = plays,
    matchId = matchId,
    set = set,
    currentScore = currentScore,
    rallyStartTime = rallyStartTime,
    rallyEndTime = rallyEndTime,
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