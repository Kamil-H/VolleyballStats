package com.kamilh.volleyballstats.clients.app.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import com.kamilh.volleyballstats.presentation.features.players.PlayerStatsPresenter
import com.kamilh.volleyballstats.presentation.features.players.PlayerStatsState
import com.kamilh.volleyballstats.ui.components.ScreenSkeleton
import com.kamilh.volleyballstats.ui.components.SelectOption
import com.kamilh.volleyballstats.ui.components.Table
import com.kamilh.volleyballstats.ui.extensions.toDp

@Composable
fun PlayerStatsScreen(
    modifier: Modifier = Modifier,
    presenter: PlayerStatsPresenter,
) {
    val state by presenter.state.collectAsState()
    PlayerStatsScreen(
        state = state,
        modifier = modifier,
    )
}

@Composable
private fun PlayerStatsScreen(
    modifier: Modifier = Modifier,
    state: PlayerStatsState,
) {
    var optionViewHeight by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()

    ScreenSkeleton(
        modifier = modifier,
        state = state,
        fabPadding = optionViewHeight.toDp(),
        listState = listState,
        onFabButtonClicked = state.onFabButtonClicked,
    ) {
        Column {
            Table(
                modifier = Modifier.weight(1f),
                verticalLazyListState = listState,
                tableContent = state.tableContent,
            )
            SelectOption(
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
                    .onGloballyPositioned { optionViewHeight = it.size.height },
                singleLine = true,
                selectOptionState = state.selectSkillState,
            )
        }
    }
}
