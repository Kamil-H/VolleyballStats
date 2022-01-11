package com.kamilh.storage.common.adapters

import com.kamilh.models.Player
import com.squareup.sqldelight.ColumnAdapter

class SpecializationAdapter : ColumnAdapter<Player.Specialization, Long> {

    override fun decode(databaseValue: Long): Player.Specialization = Player.Specialization.create(databaseValue.toInt())

    override fun encode(value: Player.Specialization): Long = value.id.toLong()
}