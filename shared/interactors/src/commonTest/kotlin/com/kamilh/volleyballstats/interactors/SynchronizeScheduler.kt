package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.models.League

fun synchronizeSchedulerOf(toSchedule: (Pair<LocalDateTime, League>) -> Unit): SynchronizeScheduler =
    SynchronizeScheduler { dateTime, league ->
        toSchedule(dateTime to league)
    }
