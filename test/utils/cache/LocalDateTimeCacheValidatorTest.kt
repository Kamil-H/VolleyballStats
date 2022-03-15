package com.kamilh.utils.cache

import com.kamilh.utils.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class LocalDateTimeCacheValidatorTest {

    private fun validatorOf(
        cacheExpiration: Duration = Duration.ZERO,
    ): LocalDateTimeCacheValidator = LocalDateTimeCacheValidator(
        cacheExpiration = cacheExpiration,
    )

    @Before
    fun updateClock() {
        CurrentDate.changeClock(testClock)
    }

    @Test
    fun `stamp is equal to current date time`() = runTest {
        // WHEN
        val stamp = validatorOf().getStamp()

        // THEN
        assertEquals(localDateTime(), stamp)
    }

    @Test
    fun `isValid returns true when checked time is within the range`() = runTest {
        // GIVEN
        val cacheExpiration = 5.minutes
        val validator = validatorOf(cacheExpiration = cacheExpiration)
        val stamp = validator.getStamp()
        val newClock = clockOf(testInstant.plus((cacheExpiration - 1.minutes)))
        CurrentDate.changeClock(newClock)

        // WHEN
        val isValid = validator.isValid(stamp)

        // THEN
        assertTrue(isValid)
    }

    @Test
    fun `isValid returns false when checked time is not within the range`() = runTest {
        // GIVEN
        val cacheExpiration = 5.minutes
        val validator = validatorOf(cacheExpiration = cacheExpiration)
        val stamp = validator.getStamp()
        val newClock = clockOf(testInstant.plus((cacheExpiration + 1.minutes)))
        CurrentDate.changeClock(newClock)

        // WHEN
        val isValid = validator.isValid(stamp)

        // THEN
        assertFalse(isValid)
    }
}

fun <STAMP> cacheValidatorOf(
    isValid: (stamp: STAMP) -> Boolean = { false },
    getStamp: () -> STAMP,
): CacheValidator<STAMP> = object : CacheValidator<STAMP> {
    override suspend fun isValid(stamp: STAMP): Boolean = isValid(stamp)

    override suspend fun getStamp(): STAMP = getStamp()
}