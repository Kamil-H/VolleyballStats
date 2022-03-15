package com.kamilh.models

import com.kamilh.datetime.LocalDateTime
import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.utils.localDateTime
import java.util.*

fun analysisInputOf(
    plays: List<AnalysisInput.Play> = listOf(),
    matchId: MatchReportId = matchReportIdOf(),
    set: Int = 0,
    score: Score = scoreOf(),
    rallyStartTime: LocalDateTime = localDateTime(),
    rallyEndTime: LocalDateTime = localDateTime(),
): AnalysisInput = AnalysisInput(
    plays = plays,
    matchId = matchId,
    set = set,
    score = score,
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