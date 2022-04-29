package com.kamilh.storage.common.adapters

import com.kamilh.models.TeamPlayer
import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class SpecializationAdapter : ColumnAdapter<TeamPlayer.Specialization, Long> {

    override fun decode(databaseValue: Long): TeamPlayer.Specialization = TeamPlayer.Specialization.create(databaseValue.toInt())

    override fun encode(value: TeamPlayer.Specialization): Long = value.id.toLong()
}