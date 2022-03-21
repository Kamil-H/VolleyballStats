package com.kamilh.models

import com.kamilh.datetime.LocalDate
import com.kamilh.datetime.LocalDateTime
import com.kamilh.repository.polishleague.seasonOf
import com.kamilh.utils.localDate
import com.kamilh.utils.localDateTime

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