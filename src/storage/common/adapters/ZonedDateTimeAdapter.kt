package com.kamilh.storage.common.adapters

import com.kamilh.datetime.ZonedDateTime
import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class ZonedDateTimeAdapter : ColumnAdapter<ZonedDateTime, String> {

    override fun decode(databaseValue: String): ZonedDateTime = ZonedDateTime.parse(databaseValue)

    override fun encode(value: ZonedDateTime): String = value.toIso8601String()
}