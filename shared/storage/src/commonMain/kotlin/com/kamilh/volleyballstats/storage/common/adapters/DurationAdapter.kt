package com.kamilh.volleyballstats.storage.common.adapters

import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Inject
class DurationAdapter : ColumnAdapter<Duration, Long> {
    override fun decode(databaseValue: Long): Duration = databaseValue.milliseconds

    override fun encode(value: Duration): Long = value.inWholeMilliseconds
}
