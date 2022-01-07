package com.kamilh.storage.common.adapters

import com.kamilh.models.TourYear
import com.squareup.sqldelight.ColumnAdapter

class TourYearAdapter : ColumnAdapter<TourYear, Long> {
    override fun decode(databaseValue: Long): TourYear = TourYear.create(databaseValue.toInt())

    override fun encode(value: TourYear): Long = value.value.toLong()
}