package com.kamilh.interactors

import java.time.LocalDateTime

fun interface SynchronizeScheduler {
    suspend fun schedule(dateTime: LocalDateTime)
}