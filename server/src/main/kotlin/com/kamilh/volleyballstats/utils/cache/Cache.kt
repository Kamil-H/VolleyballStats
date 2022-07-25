package com.kamilh.volleyballstats.utils.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface Cache<KEY, VALUE> {

    suspend fun get(key: KEY): VALUE?

    suspend fun set(key: KEY, value: VALUE)
}

class ExpirableCache<KEY, VALUE, STAMP>(
    private val cacheValidator: CacheValidator<STAMP>,
) : Cache<KEY, VALUE> {

    private val map = mutableMapOf<KEY, CacheEntry<VALUE, STAMP>>()
    private val mutex = Mutex()

    override suspend fun get(key: KEY): VALUE? = mutex.withLock {
        map[key]?.takeIf { cacheValidator.isValid(it.stamp) }?.value
    }

    override suspend fun set(key: KEY, value: VALUE) = mutex.withLock {
        map[key] = CacheEntry(value = value, stamp = cacheValidator.getStamp())
    }

    private class CacheEntry<VALUE, STAMP>(
        val value: VALUE,
        val stamp: STAMP,
    )
}
