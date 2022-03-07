package com.kamilh.storage.common.adapters

import com.kamilh.models.Season
import com.squareup.sqldelight.ColumnAdapter

class TourYearAdapter : ColumnAdapter<Season, Long> {
    override fun decode(databaseValue: Long): Season = Season.create(databaseValue.toInt())

    override fun encode(value: Season): Long = value.value.toLong()
}