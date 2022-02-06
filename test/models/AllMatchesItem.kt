package com.kamilh.models

import java.time.OffsetDateTime

fun potentiallyFinishedOf(
    id: MatchId = matchIdOf(),
    date: OffsetDateTime? = null,
    home: TeamId = teamIdOf(),
    away: TeamId = teamIdOf(),
): AllMatchesItem.PotentiallyFinished = AllMatchesItem.PotentiallyFinished(
    id = id,
    date = date,
    home = home,
    away = away,
)

fun scheduledOf(
    id: MatchId = matchIdOf(),
    date: OffsetDateTime = OffsetDateTime.now(),
    home: TeamId = teamIdOf(),
    away: TeamId = teamIdOf(),
): AllMatchesItem.Scheduled = AllMatchesItem.Scheduled(
    id = id,
    date = date,
    home = home,
    away = away,
)

fun notScheduledOf(
    id: MatchId = matchIdOf(),
    date: OffsetDateTime? = null,
    home: TeamId = teamIdOf(),
    away: TeamId = teamIdOf(),
): AllMatchesItem.NotScheduled = AllMatchesItem.NotScheduled(
    id = id,
    date = date,
    home = home,
    away = away,
)

fun savedOf(
    id: MatchId = matchIdOf(),
    date: OffsetDateTime? = null,
    home: TeamId = teamIdOf(),
    away: TeamId = teamIdOf(),
    matchReportId: MatchReportId = matchReportIdOf(),
    winnerId: TeamId = teamIdOf(),
    endTime: OffsetDateTime = OffsetDateTime.now(),
): AllMatchesItem.Saved = AllMatchesItem.Saved(
    id = id,
    date = date,
    home = home,
    away = away,
    matchReportId = matchReportId,
    winnerId = winnerId,
    endTime = endTime,
)