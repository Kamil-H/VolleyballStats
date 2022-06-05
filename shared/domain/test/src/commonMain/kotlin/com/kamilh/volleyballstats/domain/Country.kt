package com.kamilh.volleyballstats.domain

import com.kamilh.volleyballstats.domain.models.Country

fun countryOf(code: String = "PL"): Country = Country(code)