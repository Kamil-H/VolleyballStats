package com.kamilh.storage.common.adapters

import com.kamilh.models.MatchReportId
import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class MatchReportIdAdapter : ColumnAdapter<MatchReportId, Long> {
    override fun decode(databaseValue: Long): MatchReportId = MatchReportId(databaseValue)

    override fun encode(value: MatchReportId): Long = value.value
}