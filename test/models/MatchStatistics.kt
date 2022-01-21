package com.kamilh.models

import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlin.time.Duration

fun matchStatisticsOf(
    matchReportId: MatchReportId = matchReportIdOf(),
    sets: List<MatchSet> = emptyList(),
    home: MatchTeam = matchTeamOf(),
    away: MatchTeam = matchTeamOf(),
    mvp: PlayerId = playerIdOf(),
    bestPlayer: PlayerId? = null,
    updatedAt: LocalDateTime = LocalDateTime.now(),
    phase: Phase = Phase.PlayOff,
): MatchStatistics = MatchStatistics(
    matchReportId = matchReportId,
    sets = sets,
    home = home,
    away = away,
    mvp = mvp,
    bestPlayer = bestPlayer,
    updatedAt = updatedAt,
    phase = phase,
)

fun matchTeamOf(
    teamId: TeamId = teamIdOf(),
    code: String = "",
    players: List<MatchPlayer> = emptyList(),
): MatchTeam = MatchTeam(
    teamId = teamId,
    code = code,
    players = players,
)

fun matchPlayerOf(
    id: PlayerId = playerIdOf(),
    firstName: String = "",
    isForeign: Boolean? = null,
    lastName: String = "",
): MatchPlayer = MatchPlayer(
    id = id,
    firstName = firstName,
    isForeign = isForeign,
    lastName = lastName,
)

fun matchSetOf(
    number: Int = 0,
    score: Score = scoreOf(),
    points: List<MatchPoint> = emptyList(),
    startTime: OffsetDateTime = OffsetDateTime.now(),
    endTime: OffsetDateTime = OffsetDateTime.now(),
    duration: Duration = Duration.ZERO,
): MatchSet = MatchSet(
    number = number,
    score = score,
    points = points,
    startTime = startTime,
    endTime = endTime,
    duration = duration,
)

fun matchPointOf(
    score: Score = scoreOf(),
    startTime: OffsetDateTime = OffsetDateTime.now(),
    endTime: OffsetDateTime = OffsetDateTime.now(),
    playActions: List<PlayAction> = emptyList(),
    point: TeamId = teamIdOf(),
    homeLineup: Lineup = lineupOf(),
    awayLineup: Lineup = lineupOf(),
): MatchPoint = MatchPoint(
    score = score,
    startTime = startTime,
    endTime = endTime,
    playActions = playActions,
    point = point,
    homeLineup = homeLineup,
    awayLineup = awayLineup,
)