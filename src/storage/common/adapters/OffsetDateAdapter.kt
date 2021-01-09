package storage.common.adapters

import com.squareup.sqldelight.ColumnAdapter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

internal class OffsetDateAdapter : ColumnAdapter<OffsetDateTime, String> {

    override fun decode(databaseValue: String): OffsetDateTime =
        databaseValue.toOffsetDateTime()!!

    override fun encode(value: OffsetDateTime): String =
        value.toIsoString()
}

private val offsetDateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

private fun OffsetDateTime.toIsoString(): String = format(offsetDateTimeFormatter)

private fun String.toOffsetDateTime(): OffsetDateTime? =
    if (isEmpty()) null else OffsetDateTime.parse(this, offsetDateTimeFormatter)
