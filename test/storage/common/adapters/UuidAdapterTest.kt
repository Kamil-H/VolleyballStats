package com.kamilh.storage.common.adapters

import org.junit.Test
import java.util.*

class UuidAdapterTest {

    private val adapter = UuidAdapter()

    @Test
    fun `test if date is getting parsed properly`() {
        // GIVEN
        val uuid = UUID.randomUUID()

        // WHEN
        val stringUuid = adapter.encode(uuid)
        val parsedUuid = adapter.decode(stringUuid)

        // THEN
        assert(uuid == parsedUuid)
    }
}