package com.kamilh.models

import com.kamilh.datetime.ZonedDateTime
import com.kamilh.utils.zonedDateTime

fun potentiallyFinishedOf(
    id: MatchId = matchIdOf(),
    date: ZonedDateTime = zonedDateTime(),
    home: TeamId = teamIdOf(),
    away: TeamId = teamIdOf(),
): Match.PotentiallyFinished = Match.PotentiallyFinished(
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
): Match.Scheduled = Match.Scheduled(
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
): Match.NotScheduled = Match.NotScheduled(
    id = id,
    date = date,
    home = home,
    away = away,
)

fun finishedOf(
    id: MatchId = matchIdOf(),
    date: ZonedDateTime = zonedDateTime(),
    home: TeamId = teamIdOf(),
    away: TeamId = teamIdOf(),
    winnerId: TeamId = teamIdOf(),
    endTime: ZonedDateTime = zonedDateTime(),
): Match.Finished = Match.Finished(
    id = id,
    date = date,
    home = home,
    away = away,
    winnerId = winnerId,
    endTime = endTime,
)