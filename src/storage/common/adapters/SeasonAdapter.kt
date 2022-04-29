package com.kamilh.storage.common.adapters

import com.kamilh.models.Season
import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class SeasonAdapter : ColumnAdapter<Season, Long> {
    override fun decode(databaseValue: Long): Season = Season.create(databaseValue.toInt())

    override fun encode(value: Season): Long = value.value.toLong()
}