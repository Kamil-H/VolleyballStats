package com.kamilh.volleyballstats.utils.cache

import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

interface CacheValidator<STAMP> {

    suspend fun isValid(stamp: STAMP): Boolean

    suspend fun getStamp(): STAMP
}
private val playersCacheValidity = 6.hours
class LocalDateTimeCacheValidator(
    private val cacheExpiration: Duration = playersCacheValidity,
) : CacheValidator<LocalDateTime> {

    override suspend fun isValid(stamp: LocalDateTime): Boolean =
        CurrentDate.localDateTime < stamp.plus(cacheExpiration)

    override suspend fun getStamp(): LocalDateTime = CurrentDate.localDateTime
}
