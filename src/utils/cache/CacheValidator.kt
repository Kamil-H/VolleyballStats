package com.kamilh.utils.cache

import com.kamilh.datetime.LocalDateTime
import com.kamilh.utils.CurrentDate
import kotlin.time.Duration

interface CacheValidator<STAMP> {

    suspend fun isValid(stamp: STAMP): Boolean

    suspend fun getStamp(): STAMP
}

class LocalDateTimeCacheValidator(
    private val cacheExpiration: Duration,
) : CacheValidator<LocalDateTime> {

    override suspend fun isValid(stamp: LocalDateTime): Boolean =
        CurrentDate.localDateTime < stamp.plus(cacheExpiration)

    override suspend fun getStamp(): LocalDateTime = CurrentDate.localDateTime
}