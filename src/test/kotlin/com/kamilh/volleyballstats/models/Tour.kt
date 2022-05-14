package com.kamilh.volleyballstats.models

import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.repository.polishleague.seasonOf
import com.kamilh.volleyballstats.utils.localDate
import com.kamilh.volleyballstats.utils.localDateTime

fun tourOf(
    id: TourId = tourIdOf(),
    name: String = "",
    season: Season = seasonOf(),
    league: League = leagueOf(),
    startDate: LocalDate = localDate(),
    endDate: LocalDate? = null,
    updatedAt: LocalDateTime = localDateTime(),
): Tour = Tour(
    id = id,
    name = name,
    season = season,
    league = league,
    startDate = startDate,
    endDate = endDate,
    updatedAt = updatedAt,
)