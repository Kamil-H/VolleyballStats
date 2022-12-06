package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.models.League

fun synchronizeSchedulerOf(toSchedule: (Pair<ZonedDateTime, League>) -> Unit): SynchronizeScheduler =
    SynchronizeScheduler { dateTime, league ->
        toSchedule(dateTime to league)
    }
