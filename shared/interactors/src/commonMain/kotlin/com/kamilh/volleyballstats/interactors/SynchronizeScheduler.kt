package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.models.League

fun interface SynchronizeScheduler {

    fun schedule(dateTime: ZonedDateTime, league: League)
}
