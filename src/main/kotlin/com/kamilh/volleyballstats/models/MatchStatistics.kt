package com.kamilh.volleyballstats.models

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.datetime.ZonedDateTime
import kotlin.time.Duration

data class MatchStatistics(
    val matchId: MatchId,
    val sets: List<MatchSet>,
    val home: MatchTeam,
    val away: MatchTeam,
    val mvp: PlayerId,
    val bestPlayer: PlayerId?,
    val updatedAt: LocalDateTime,
    val phase: Phase,
)

data class MatchSet(
    val number: Int,
    val score: Score,
    val points: List<MatchPoint>,
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime,
    val duration: Duration,
)

data class MatchPoint(
    val score: Score,
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime,
    val playActions: List<PlayAction> = emptyList(),
    val point: TeamId,
    val homeLineup: Lineup,
    val awayLineup: Lineup,
)