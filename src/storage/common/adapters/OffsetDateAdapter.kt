package storage.common.adapters

import com.kamilh.extensions.toIsoString
import com.kamilh.extensions.toOffsetDateTime
import com.squareup.sqldelight.ColumnAdapter
import java.time.OffsetDateTime

internal class OffsetDateAdapter : ColumnAdapter<OffsetDateTime, String> {

    override fun decode(databaseValue: String): OffsetDateTime =
        databaseValue.toOffsetDateTime()!!

    override fun encode(value: OffsetDateTime): String =
        value.toIsoString()
}