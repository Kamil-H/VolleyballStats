package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.datetime.LocalDateTime

fun interface SynchronizeScheduler {
    suspend fun schedule(dateTime: LocalDateTime)
}
