package storage.common.adapters

import com.kamilh.datetime.ZonedDateTime
import com.squareup.sqldelight.ColumnAdapter

internal class ZonedDateTimeAdapter : ColumnAdapter<ZonedDateTime, String> {

    override fun decode(databaseValue: String): ZonedDateTime =
        ZonedDateTime.parse(databaseValue)

    override fun encode(value: ZonedDateTime): String =
        value.toIso8601String()
}