package com.kamilh.volleyballstats.presentation.navigation

import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.domain.models.stats.StatsType
import com.kamilh.volleyballstats.presentation.parcelable.Parcelable
import com.kamilh.volleyballstats.presentation.parcelable.Parcelize

sealed interface NavigationEvent : Parcelable {

    @Parcelize
    data class PlayerFiltersRequested(val skill: StatsSkill, val type: StatsType) : NavigationEvent

    @Parcelize
    data object Close : NavigationEvent

    @Parcelize
    data object HomeTabRequested : NavigationEvent

    @Parcelize
    data object PlayersTabRequested : NavigationEvent

    @Parcelize
    data object TeamsTabRequested : NavigationEvent
}
