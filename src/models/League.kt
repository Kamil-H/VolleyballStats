package com.kamilh.models

data class League(
    val country: Country,
    val division: Int,
) {

    companion object {
        val POLISH_LEAGUE: League = League(
            country = Country.POLAND,
            division = 1
        )
    }
}
