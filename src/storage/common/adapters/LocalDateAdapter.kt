package com.kamilh.storage.common.adapters

import com.kamilh.extensions.toIsoString
import com.kamilh.extensions.toLocalDate
import com.squareup.sqldelight.ColumnAdapter
import java.time.LocalDate

internal class LocalDateAdapter : ColumnAdapter<LocalDate, String> {

    override fun decode(databaseValue: String): LocalDate =
        databaseValue.toLocalDate()!!

    override fun encode(value: LocalDate): String =
        value.toIsoString()
}