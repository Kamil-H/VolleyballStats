package com.kamilh.storage.common.adapters

import com.kamilh.models.MatchId
import com.squareup.sqldelight.ColumnAdapter

class MatchIdAdapter : ColumnAdapter<MatchId, Long> {
    override fun decode(databaseValue: Long): MatchId = MatchId(databaseValue)

    override fun encode(value: MatchId): Long = value.value
}