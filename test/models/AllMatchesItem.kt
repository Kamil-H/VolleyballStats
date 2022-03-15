package com.kamilh.models

import com.kamilh.datetime.ZonedDateTime
import com.kamilh.utils.zonedDateTime

fun potentiallyFinishedOf(
    id: MatchId = matchIdOf(),
    date: ZonedDateTime? = null,
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
    date: ZonedDateTime = zonedDateTime(),
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
    date: ZonedDateTime? = null,
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
    date: ZonedDateTime? = null,
    home: TeamId = teamIdOf(),
    away: TeamId = teamIdOf(),
    matchReportId: MatchReportId = matchReportIdOf(),
    winnerId: TeamId = teamIdOf(),
    endTime: ZonedDateTime = zonedDateTime(),
): AllMatchesItem.Saved = AllMatchesItem.Saved(
    id = id,
    date = date,
    home = home,
    away = away,
    matchReportId = matchReportId,
    winnerId = winnerId,
    endTime = endTime,
)