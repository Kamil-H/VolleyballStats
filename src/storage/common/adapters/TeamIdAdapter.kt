package com.kamilh.storage.common.adapters

import com.kamilh.models.TeamId
import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class TeamIdAdapter : ColumnAdapter<TeamId, Long> {
    override fun decode(databaseValue: Long): TeamId = TeamId(databaseValue)

    override fun encode(value: TeamId): Long = value.value
}