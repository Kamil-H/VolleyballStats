package com.kamilh.storage.common.adapters

import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration

@Inject
class DurationAdapter : ColumnAdapter<Duration, Long> {
    override fun decode(databaseValue: Long): Duration = Duration.milliseconds(databaseValue)

    override fun encode(value: Duration): Long = value.inWholeMilliseconds
}