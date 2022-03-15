package com.kamilh.interactors

import com.kamilh.datetime.LocalDateTime

fun interface SynchronizeScheduler {
    suspend fun schedule(dateTime: LocalDateTime)
}