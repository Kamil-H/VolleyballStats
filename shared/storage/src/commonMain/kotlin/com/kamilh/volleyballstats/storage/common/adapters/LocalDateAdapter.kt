package com.kamilh.volleyballstats.storage.common.adapters

import com.kamilh.volleyballstats.datetime.LocalDate
import app.cash.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class LocalDateAdapter : ColumnAdapter<LocalDate, String> {

    override fun decode(databaseValue: String): LocalDate =
        LocalDate.parse(databaseValue)

    override fun encode(value: LocalDate): String =
        value.toIso8601String()
}
