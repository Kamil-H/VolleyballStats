package com.kamilh.volleyballstats.presentation.features.main

import com.kamilh.volleyballstats.presentation.features.common.Icon

data class MainState(
    val bottomItems: List<BottomItemState>,
)

data class BottomItemState(
    val id: BottomMenuItem,
    val label: String,
    val icon: Icon,
    val selected: Boolean,
    val onClicked: (BottomMenuItem) -> Unit,
)

enum class BottomMenuItem(val icon: Icon, val label: String) {
    Home(
        icon = Icon.Scoreboard,
        label = "Home",
    ),
    Players(
        icon = Icon.Person,
        label = "Players",
    ),
    Teams(
        icon = Icon.Groups,
        label = "Teams",
    ),
}

fun List<BottomItemState>.select(id: BottomMenuItem): List<BottomItemState> =
    map { item -> item.copy(selected = item.id == id) }
