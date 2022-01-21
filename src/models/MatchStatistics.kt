package com.kamilh.models

import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlin.time.Duration

data class MatchStatistics(
    val matchReportId: MatchReportId,
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
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val duration: Duration,
)

data class MatchPoint(
    val score: Score,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val playActions: List<PlayAction> = emptyList(),
    val point: TeamId,
    val homeLineup: Lineup,
    val awayLineup: Lineup,
)