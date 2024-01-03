package com.kamilh.volleyballstats.presentation.navigation

import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.domain.models.stats.StatsType
import com.kamilh.volleyballstats.presentation.parcelable.Parcelable
import com.kamilh.volleyballstats.presentation.parcelable.Parcelize

sealed interface Screen : Parcelable {

    sealed interface Full : Screen {
        @Parcelize
        data object Root : Full
    }

    sealed interface Tab : Screen

    @Parcelize
    data object Home : Tab

    @Parcelize
    data class Stats(val type: StatsType) : Tab

    @Parcelize
    data class Filters(
        val skill: StatsSkill,
        val type: StatsType,
    ) : Full

    @Parcelize
    data object Main : Screen
}
