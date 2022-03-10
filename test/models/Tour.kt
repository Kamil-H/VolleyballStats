package com.kamilh.models

import com.kamilh.repository.polishleague.seasonOf
import java.time.LocalDate
import java.time.LocalDateTime

fun tourOf(
    id: TourId = tourIdOf(),
    name: String = "",
    season: Season = seasonOf(),
    league: League = leagueOf(),
    startDate: LocalDate = LocalDate.now(),
    endDate: LocalDate? = null,
    winnerId: TeamId? = teamIdOf(),
    updatedAt: LocalDateTime = LocalDateTime.now(),
): Tour = Tour(
    id = id,
    name = name,
    season = season,
    league = league,
    startDate = startDate,
    endDate = endDate,
    winnerId = winnerId,
    updatedAt = updatedAt,
)