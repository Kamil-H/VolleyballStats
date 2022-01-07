package com.kamilh.models

import com.kamilh.repository.polishleague.tourYearOf
import java.time.LocalDate
import java.time.LocalDateTime

fun tourOf(
    name: String = "",
    year: TourYear = tourYearOf(),
    league: League = leagueOf(),
    startDate: LocalDate = LocalDate.now(),
    endDate: LocalDate? = null,
    winnerId: TeamId? = teamIdOf(),
    updatedAt: LocalDateTime = LocalDateTime.now(),
): Tour = Tour(
    name = name,
    year = year,
    league = league,
    startDate = startDate,
    endDate = endDate,
    winnerId = winnerId,
    updatedAt = updatedAt,
)