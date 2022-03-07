package com.kamilh.utils.cache

import com.kamilh.utils.CurrentDate
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.toJavaDuration

interface CacheValidator<STAMP> {

    suspend fun isValid(stamp: STAMP): Boolean

    suspend fun getStamp(): STAMP
}

class LocalDateTimeCacheValidator(
    private val cacheExpiration: Duration,
) : CacheValidator<LocalDateTime> {

    override suspend fun isValid(stamp: LocalDateTime): Boolean =
        CurrentDate.localDateTime < stamp.plus(cacheExpiration.toJavaDuration())

    override suspend fun getStamp(): LocalDateTime = CurrentDate.localDateTime
}