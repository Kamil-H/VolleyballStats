package com.kamilh.storage.common.adapters

import com.squareup.sqldelight.ColumnAdapter
import kotlin.time.Duration

class DurationAdapter : ColumnAdapter<Duration, Long> {
    override fun decode(databaseValue: Long): Duration = Duration.milliseconds(databaseValue)

    override fun encode(value: Duration): Long = value.inWholeMilliseconds
}