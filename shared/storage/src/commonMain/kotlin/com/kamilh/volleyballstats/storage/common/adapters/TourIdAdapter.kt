package com.kamilh.volleyballstats.storage.common.adapters

import com.kamilh.volleyballstats.domain.models.TourId
import app.cash.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class TourIdAdapter : ColumnAdapter<TourId, Long> {
    override fun decode(databaseValue: Long): TourId = TourId(databaseValue)

    override fun encode(value: TourId): Long = value.value
}
