package com.kamilh.models

fun matchIdOf(matchId: Long = 0): MatchId = MatchId(matchId)

fun matchReportIdOf(matchReportId: Long = 0): MatchReportId = MatchReportId(matchReportId)

fun playerIdOf(value: Int = 0): PlayerId = PlayerId(value.toLong())

fun teamIdOf(value: Long = 0): TeamId = TeamId(value)