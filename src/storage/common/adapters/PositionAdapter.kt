package com.kamilh.storage.common.adapters

import com.kamilh.models.PlayerPosition
import com.squareup.sqldelight.ColumnAdapter

class PositionAdapter : ColumnAdapter<PlayerPosition, Long> {
    override fun decode(databaseValue: Long): PlayerPosition = PlayerPosition.create(databaseValue.toInt())

    override fun encode(value: PlayerPosition): Long = value.value.toLong()
}