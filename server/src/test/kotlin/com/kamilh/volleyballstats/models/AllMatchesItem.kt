package com.kamilh.volleyballstats.models

import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.utils.zonedDateTime

fun potentiallyFinishedOf(
    id: MatchId = matchIdOf(),
    date: ZonedDateTime = zonedDateTime(),
    home: TeamId = teamIdOf(),
    away: TeamId = teamIdOf(),
): MatchInfo.PotentiallyFinished = MatchInfo.PotentiallyFinished(
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
): MatchInfo.Scheduled = MatchInfo.Scheduled(
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
): MatchInfo.NotScheduled = MatchInfo.NotScheduled(
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
): MatchInfo.Finished = MatchInfo.Finished(
    id = id,
    date = date,
    home = home,
    away = away,
    winnerId = winnerId,
    endTime = endTime,
)