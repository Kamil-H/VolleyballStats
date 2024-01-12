package com.kamilh.volleyballstats.presentation.features.main

import com.kamilh.volleyballstats.presentation.Resources
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
        label = Resources.string.main_bottom_menu_home_label,
    ),
    Players(
        icon = Icon.Person,
        label = Resources.string.main_bottom_menu_players_label,
    ),
    Teams(
        icon = Icon.Groups,
        label = Resources.string.main_bottom_menu_teams_label,
    ),
}

fun List<BottomItemState>.select(tabIndex: Int): List<BottomItemState> =
    map { item -> item.copy(selected = item.id == BottomMenuItem.entries[tabIndex]) }
