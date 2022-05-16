package com.kamilh.volleyballstats.models

fun leagueOf(
    country: Country = countryOf(),
    division: Int = 0,
): League = League(
    country = country,
    division = division,
)