package com.kamilh.storage.common.adapters

import com.kamilh.models.Phase
import com.squareup.sqldelight.ColumnAdapter

class PhaseAdapter : ColumnAdapter<Phase, String> {
    override fun decode(databaseValue: String): Phase = Phase.create(databaseValue)

    override fun encode(value: Phase): String = value.id.first()
}