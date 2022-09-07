package com.kamilh.volleyballstats.storage.common.adapters

import com.kamilh.volleyballstats.domain.models.Specialization
import com.squareup.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class SpecializationAdapter : ColumnAdapter<Specialization, String> {

    override fun decode(databaseValue: String): Specialization =
        when (databaseValue) {
            SPECIALIZATION_SETTER -> Specialization.Setter
            SPECIALIZATION_LIBERO -> Specialization.Libero
            SPECIALIZATION_MIDDLE -> Specialization.MiddleBlocker
            SPECIALIZATION_OUTSIDE -> Specialization.OutsideHitter
            SPECIALIZATION_OPPOSITE -> Specialization.OppositeHitter
            else -> error("Wrong databaseValue: $databaseValue")
        }

    override fun encode(value: Specialization): String =
        when (value) {
            Specialization.Setter -> SPECIALIZATION_SETTER
            Specialization.Libero -> SPECIALIZATION_LIBERO
            Specialization.MiddleBlocker -> SPECIALIZATION_MIDDLE
            Specialization.OutsideHitter -> SPECIALIZATION_OUTSIDE
            Specialization.OppositeHitter -> SPECIALIZATION_OPPOSITE
        }

    companion object {
        private const val SPECIALIZATION_SETTER = "S"
        private const val SPECIALIZATION_LIBERO = "L"
        private const val SPECIALIZATION_MIDDLE = "MB"
        private const val SPECIALIZATION_OUTSIDE = "OH"
        private const val SPECIALIZATION_OPPOSITE = "OP"
    }
}
