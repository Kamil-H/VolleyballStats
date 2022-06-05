package com.kamilh.volleyballstats.domain

import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.TourId

fun matchIdOf(value: Long = 0): MatchId = MatchId(value)

fun playerIdOf(value: Int = 0): PlayerId = PlayerId(value.toLong())

fun teamIdOf(value: Long = 0): TeamId = TeamId(value)

fun tourIdOf(value: Long = 0): TourId = TourId(value)