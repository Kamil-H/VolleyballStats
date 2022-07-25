package com.kamilh.volleyballstats.storage.common.adapters

import com.kamilh.volleyballstats.domain.models.PlayerId
import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class PlayerIdAdapter : ColumnAdapter<PlayerId, Long> {
    override fun decode(databaseValue: Long): PlayerId = PlayerId(databaseValue)

    override fun encode(value: PlayerId): Long = value.value
}
