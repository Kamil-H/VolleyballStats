package com.kamilh.volleyballstats.presentation.navigation

import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.presentation.parcelable.Parcelable
import com.kamilh.volleyballstats.presentation.parcelable.Parcelize

sealed interface NavigationEvent : Parcelable {

    @Parcelize
    data class PlayerFiltersRequested(val skill: StatsSkill) : NavigationEvent

    @Parcelize
    object Close : NavigationEvent

    @Parcelize
    object HomeTabRequested : NavigationEvent

    @Parcelize
    object PlayersTabRequested : NavigationEvent

    @Parcelize
    object TeamsTabRequested : NavigationEvent
}
