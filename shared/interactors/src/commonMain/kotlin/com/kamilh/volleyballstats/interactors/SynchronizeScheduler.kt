package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.models.League

fun interface SynchronizeScheduler {

    fun schedule(dateTime: LocalDateTime, league: League)
}
