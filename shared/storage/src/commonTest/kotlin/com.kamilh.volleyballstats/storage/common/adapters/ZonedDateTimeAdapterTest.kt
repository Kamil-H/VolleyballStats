package com.kamilh.volleyballstats.storage.common.adapters

import com.kamilh.volleyballstats.utils.zonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

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
        assertEquals(expected = date, parsedDate)
    }
}