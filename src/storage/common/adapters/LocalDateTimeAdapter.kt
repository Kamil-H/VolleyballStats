package com.kamilh.storage.common.adapters

import com.kamilh.extensions.toIsoString
import com.kamilh.extensions.toLocalDateTime
import com.squareup.sqldelight.ColumnAdapter
import java.time.LocalDateTime

internal class LocalDateTimeAdapter : ColumnAdapter<LocalDateTime, String> {

    override fun decode(databaseValue: String): LocalDateTime =
        databaseValue.toLocalDateTime()!!

    override fun encode(value: LocalDateTime): String =
        value.toIsoString()
}