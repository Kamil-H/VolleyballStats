package com.kamilh.volleyballstats.presentation.navigation

import com.kamilh.volleyballstats.presentation.parcelable.Parcelable
import com.kamilh.volleyballstats.presentation.parcelable.Parcelize

sealed interface NavigationEvent : Parcelable {

    @Parcelize
    data class GoTo(val screen: Screen) : NavigationEvent

    @Parcelize
    data object Close : NavigationEvent
}
