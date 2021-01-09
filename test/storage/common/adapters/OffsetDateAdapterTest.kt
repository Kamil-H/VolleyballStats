package com.kamilh.storage.common.adapters

import org.junit.Test
import storage.common.adapters.OffsetDateAdapter
import java.time.OffsetDateTime

class OffsetDateAdapterTest {

    private val adapter = OffsetDateAdapter()

    @Test
    fun `test if date is getting parsed properly`() {
        // GIVEN
        val date = OffsetDateTime.now()

        // WHEN
        val stringDate = adapter.encode(date)
        val parsedDate = adapter.decode(stringDate)

        // THEN
        assert(date == parsedDate)
    }
}