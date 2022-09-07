package com.kamilh.volleyballstats.storage.common.adapters

import com.kamilh.volleyballstats.domain.models.Effect
import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class EffectAdapter : ColumnAdapter<Effect, String> {

    override fun decode(databaseValue: String): Effect =
        when (databaseValue) {
            EFFECT_PERFECT -> Effect.Perfect
            EFFECT_POSITIVE -> Effect.Positive
            EFFECT_NEGATIVE -> Effect.Negative
            EFFECT_ERROR -> Effect.Error
            EFFECT_HALF -> Effect.Half
            EFFECT_INVASION -> Effect.Invasion
            else -> error("Wrong databaseValue: $databaseValue")
        }

    override fun encode(value: Effect): String =
        when (value) {
            Effect.Perfect -> EFFECT_PERFECT
            Effect.Positive -> EFFECT_POSITIVE
            Effect.Negative -> EFFECT_NEGATIVE
            Effect.Error -> EFFECT_ERROR
            Effect.Half -> EFFECT_HALF
            Effect.Invasion -> EFFECT_INVASION
        }

    companion object {
        private const val EFFECT_PERFECT = "#"
        private const val EFFECT_POSITIVE = "+"
        private const val EFFECT_NEGATIVE = "-"
        private const val EFFECT_ERROR = "="
        private const val EFFECT_HALF = "/"
        private const val EFFECT_INVASION = "!"
    }
}
