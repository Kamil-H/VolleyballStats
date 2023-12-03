package com.kamilh.volleyballstats.storage.common.adapters

import com.kamilh.volleyballstats.domain.models.Phase
import app.cash.sqldelight.ColumnAdapter
import me.tatarka.inject.annotations.Inject

@Inject
class PhaseAdapter : ColumnAdapter<Phase, String> {

    override fun decode(databaseValue: String): Phase =
        when (databaseValue) {
            PHASE_PLAY_OFF -> Phase.PlayOff
            PHASE_REGULAR_SEASON -> Phase.RegularSeason
            else -> error("Wrong databaseValue: $databaseValue")
        }

    override fun encode(value: Phase): String =
        when (value) {
            Phase.PlayOff -> PHASE_PLAY_OFF
            Phase.RegularSeason -> PHASE_REGULAR_SEASON
        }

    companion object {
        private const val PHASE_PLAY_OFF = "PO"
        private const val PHASE_REGULAR_SEASON = "RS"
    }
}
