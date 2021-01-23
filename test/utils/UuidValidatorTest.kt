package com.kamilh.utils

import com.kamilh.storage.nullUUID
import org.junit.Test

class UuidValidatorTest {

    private val uuidValidator = JavaUtilUuidValidator()

    @Test
    fun `test if validator returns false when passing invalid UUID`() {
        // GIVEN
        val uuid = ""

        // WHEN
        val isValid = uuidValidator.isValid(uuid)

        // THEN
        assert(!isValid)
    }

    @Test
    fun `test if validator returns true when passing valid UUID`() {
        // GIVEN
        val uuid = nullUUID().toString()

        // WHEN
        val isValid = uuidValidator.isValid(uuid)

        // THEN
        assert(isValid)
    }
}