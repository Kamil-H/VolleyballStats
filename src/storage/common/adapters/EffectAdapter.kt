package com.kamilh.storage.common.adapters

import com.kamilh.models.Effect
import com.squareup.sqldelight.ColumnAdapter

class EffectAdapter : ColumnAdapter<Effect, String> {
    override fun decode(databaseValue: String): Effect = Effect.create(databaseValue)

    override fun encode(value: Effect): String = value.id.toString()
}