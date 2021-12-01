package com.kamilh.models

import java.time.LocalDateTime
import kotlin.time.Duration

data class MatchStatistics(
    val matchReportId: MatchReportId,
    val sets: List<MatchSet>,
    val home: TeamId,
    val away: TeamId,
    val mvp: PlayerId,
    val bestPlayer: PlayerId?,
)

data class MatchSet(
    val score: Score,
    val points: List<MatchPoint>,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val duration: Duration,
)

data class MatchPoint(
    val score: Score,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val playActions: List<PlayAction> = emptyList(),
    val point: TeamType,
    val homeLineup: Lineup,
    val awayLineup: Lineup,
)