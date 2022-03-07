package com.kamilh.utils.cache

import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExpirableCacheTest {

    private fun cacheOf(
        cacheValidator: CacheValidator<String> = cacheValidatorOf { "" }
    ): ExpirableCache<String, Int, String> = ExpirableCache(
        cacheValidator = cacheValidator,
    )

    @Test
    fun `cache returns null when set hasn't been called`() = runTest {
        // GIVEN
        val key = "key"

        // WHEN
        val cached = cacheOf().get(key)

        // THEN
        assertNull(cached)
    }

    @Test
    fun `cache returns null when cacheValidator returns false`() = runTest {
        // GIVEN
        val isValid = false
        val value = 1
        val key = "key"

        // WHEN
        val cache = cacheOf(cacheValidator = cacheValidatorOf(isValid = { isValid }) { "" })
        cache.set(key, value)
        val cached = cache.get(key)

        // THEN
        assertNull(cached)
    }

    @Test
    fun `cache returns value when cacheValidator returns true`() = runTest {
        // GIVEN
        val isValid = true
        val value = 1
        val key = "key"

        // WHEN
        val cache = cacheOf(cacheValidator = cacheValidatorOf(isValid = { isValid }) { "" })
        cache.set(key, value)
        val cached = cache.get(key)

        // THEN
        assertEquals(value, cached)
    }

    @Test
    fun `cache overrides value`() = runTest {
        // GIVEN
        val isValid = true
        val value = 1
        val newValue = 2
        val key = "key"

        // WHEN
        val cache = cacheOf(cacheValidator = cacheValidatorOf(isValid = { isValid }) { "" })
        cache.set(key, value)
        cache.set(key, newValue)
        val cached = cache.get(key)

        // THEN
        assertEquals(newValue, cached)
    }

    @Test
    fun `cache doesn't return value when there is a value saved under different key`() = runTest {
        // GIVEN
        val isValid = true
        val value = 1
        val key = "key"
        val newKey = "newKey"

        // WHEN
        val cache = cacheOf(cacheValidator = cacheValidatorOf(isValid = { isValid }) { "" })
        cache.set(key, value)
        val cached = cache.get(newKey)

        // THEN
        assertNull(cached)
    }
}

fun <KEY, VALUE> cacheOf(
    get: VALUE? = null,
    set: (key: KEY, value: VALUE) -> Unit = { _, _ -> },
): Cache<KEY, VALUE> = object : Cache<KEY, VALUE> {

    override suspend fun get(key: KEY): VALUE? = get
    override suspend fun set(key: KEY, value: VALUE) = set(key, value)
}