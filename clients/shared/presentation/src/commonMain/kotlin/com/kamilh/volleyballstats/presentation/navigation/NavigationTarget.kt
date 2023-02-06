package com.kamilh.volleyballstats.presentation.navigation

import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.domain.models.stats.StatsType
import com.kamilh.volleyballstats.presentation.parcelable.Parcelable
import com.kamilh.volleyballstats.presentation.parcelable.Parcelize

@Parcelize
enum class TabTarget : Parcelable {
    Home, PlayersStats, TeamsStats
}

sealed interface BackStackTarget : Parcelable {

    @Parcelize
    object Root : BackStackTarget

    @Parcelize
    data class PlayerFilters(
        val skill: StatsSkill,
        val type: StatsType,
    ) : BackStackTarget
}
