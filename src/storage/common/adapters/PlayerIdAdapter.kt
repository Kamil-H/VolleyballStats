package com.kamilh.storage.common.adapters

import com.kamilh.models.PlayerId
import com.squareup.sqldelight.ColumnAdapter

class PlayerIdAdapter : ColumnAdapter<PlayerId, Long> {
    override fun decode(databaseValue: Long): PlayerId = PlayerId(databaseValue)

    override fun encode(value: PlayerId): Long = value.value
}