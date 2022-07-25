package com.kamilh.volleyballstats.storage.common.adapters

import com.kamilh.volleyballstats.domain.models.MatchId
import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class MatchIdAdapter : ColumnAdapter<MatchId, Long> {
    override fun decode(databaseValue: Long): MatchId = MatchId(databaseValue)

    override fun encode(value: MatchId): Long = value.value
}
