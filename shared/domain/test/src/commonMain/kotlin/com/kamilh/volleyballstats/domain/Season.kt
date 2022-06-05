package com.kamilh.volleyballstats.domain

import com.kamilh.volleyballstats.domain.models.Season

fun seasonOf(tour: Int = 2020): Season = Season.create(tour)