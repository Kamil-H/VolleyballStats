package com.kamilh.models

import com.kamilh.datetime.LocalDateTime
import com.kamilh.datetime.ZonedDateTime
import com.kamilh.utils.localDateTime
import com.kamilh.utils.zonedDateTime
import kotlin.time.Duration

fun matchStatisticsOf(
    matchReportId: MatchReportId = matchReportIdOf(),
    sets: List<MatchSet> = emptyList(),
    home: MatchTeam = matchTeamOf(),
    away: MatchTeam = matchTeamOf(),
    mvp: PlayerId = playerIdOf(),
    bestPlayer: PlayerId? = null,
    updatedAt: LocalDateTime = localDateTime(),
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
    players: List<PlayerId> = emptyList(),
): MatchTeam = MatchTeam(
    teamId = teamId,
    code = code,
    players = players,
)

fun matchSetOf(
    number: Int = 0,
    score: Score = scoreOf(),
    points: List<MatchPoint> = emptyList(),
    startTime: ZonedDateTime = zonedDateTime(),
    endTime: ZonedDateTime = zonedDateTime(),
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
    startTime: ZonedDateTime = zonedDateTime(),
    endTime: ZonedDateTime = zonedDateTime(),
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