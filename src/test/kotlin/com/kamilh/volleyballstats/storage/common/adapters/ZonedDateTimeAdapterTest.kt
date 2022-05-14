package com.kamilh.volleyballstats.storage.common.adapters

import com.kamilh.volleyballstats.utils.zonedDateTime
import org.junit.Test

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