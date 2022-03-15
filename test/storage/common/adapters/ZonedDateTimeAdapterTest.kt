package com.kamilh.storage.common.adapters

import com.kamilh.utils.zonedDateTime
import org.junit.Test
import storage.common.adapters.ZonedDateTimeAdapter

class ZonedDateTimeAdapterTest {

    private val adapter = ZonedDateTimeAdapter()

    @Test
    fun `test if date is getting parsed properly`() {
        // GIVEN
        val date = zonedDateTime()

        // WHEN
        val stringDate = adapter.encode(date)
        val parsedDate = adapter.decode(stringDate)

        // THEN
        assert(date == parsedDate)
    }
}