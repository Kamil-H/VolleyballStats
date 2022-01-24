package com.kamilh.models

import java.time.LocalDateTime
import java.time.OffsetDateTime

fun potentiallyFinishedOf(id: MatchId = matchIdOf()): AllMatchesItem.PotentiallyFinished = AllMatchesItem.PotentiallyFinished(id = id)

fun scheduledOf(
    id: MatchId = matchIdOf(),
    date: LocalDateTime = LocalDateTime.now(),
): AllMatchesItem.Scheduled = AllMatchesItem.Scheduled(
    id = id,
    date = date,
)

fun notScheduledOf(id: MatchId = matchIdOf()): AllMatchesItem.NotScheduled = AllMatchesItem.NotScheduled(id = id)

fun savedOf(
    id: MatchId = matchIdOf(),
    matchReportId: MatchReportId = matchReportIdOf(),
    winnerId: TeamId = teamIdOf(),
    endTime: OffsetDateTime = OffsetDateTime.now(),
): AllMatchesItem.Saved = AllMatchesItem.Saved(
    id = id,
    matchReportId = matchReportId,
    winnerId = winnerId,
    endTime = endTime,
)