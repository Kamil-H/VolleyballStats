package com.kamilh.volleyballstats.models

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.utils.localDateTime
import com.kamilh.volleyballstats.utils.zonedDateTime
import kotlin.time.Duration

fun matchStatisticsOf(
    matchId: MatchId = matchIdOf(),
    sets: List<MatchSet> = emptyList(),
    home: MatchTeam = matchTeamOf(),
    away: MatchTeam = matchTeamOf(),
    mvp: PlayerId = playerIdOf(),
    bestPlayer: PlayerId? = null,
    updatedAt: LocalDateTime = localDateTime(),
    phase: Phase = Phase.PlayOff,
): MatchStatistics = MatchStatistics(
    matchId = matchId,
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