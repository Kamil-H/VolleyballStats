package com.kamilh.volleyballstats.storage.common.adapters

import com.kamilh.volleyballstats.datetime.LocalDateTime
import app.cash.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class LocalDateTimeAdapter : ColumnAdapter<LocalDateTime, String> {

    override fun decode(databaseValue: String): LocalDateTime =
        LocalDateTime.parse(databaseValue)

    override fun encode(value: LocalDateTime): String =
        value.toIso8601String()
}
