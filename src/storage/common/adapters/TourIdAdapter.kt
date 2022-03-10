package com.kamilh.storage.common.adapters

import com.kamilh.models.TourId
import com.squareup.sqldelight.ColumnAdapter

class TourIdAdapter : ColumnAdapter<TourId, Long> {
    override fun decode(databaseValue: Long): TourId = TourId(databaseValue)

    override fun encode(value: TourId): Long = value.value
}