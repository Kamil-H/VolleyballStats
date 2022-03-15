package com.kamilh.storage.common.adapters

import com.kamilh.datetime.LocalDate
import com.squareup.sqldelight.ColumnAdapter

internal class LocalDateAdapter : ColumnAdapter<LocalDate, String> {

    override fun decode(databaseValue: String): LocalDate =
        LocalDate.parse(databaseValue)

    override fun encode(value: LocalDate): String =
        value.toIso8601String()
}