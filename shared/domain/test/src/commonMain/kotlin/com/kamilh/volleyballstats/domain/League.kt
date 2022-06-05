package com.kamilh.volleyballstats.domain

import com.kamilh.volleyballstats.domain.models.Country
import com.kamilh.volleyballstats.domain.models.League

fun leagueOf(
    country: Country = countryOf(),
    division: Int = 0,
): League = League(
    country = country,
    division = division,
)