package com.kamilh.volleyballstats.models

fun matchIdOf(matchId: Long = 0): MatchId = MatchId(matchId)

fun matchReportIdOf(matchReportId: Long = 0): MatchReportId = MatchReportId(matchReportId)

fun playerIdOf(value: Int = 0): PlayerId = PlayerId(value.toLong())

fun teamIdOf(value: Long = 0): TeamId = TeamId(value)

fun tourIdOf(value: Long = 0): TourId = TourId(value)