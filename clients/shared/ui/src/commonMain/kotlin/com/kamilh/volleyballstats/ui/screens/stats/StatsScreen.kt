package com.kamilh.volleyballstats.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import com.kamilh.volleyballstats.presentation.features.stats.StatsPresenter
import com.kamilh.volleyballstats.presentation.features.stats.StatsState
import com.kamilh.volleyballstats.ui.components.ScreenSkeleton
import com.kamilh.volleyballstats.ui.components.SelectOption
import com.kamilh.volleyballstats.ui.components.Table
import com.kamilh.volleyballstats.ui.extensions.toDp

@Composable
fun StatsScreen(
    presenter: StatsPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()
    StatsScreen(
        state = state,
        onFabButtonClicked = presenter::onFabButtonClicked,
        modifier = modifier,
    )
}

@Composable
private fun StatsScreen(
    state: StatsState,
    onFabButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var optionViewHeight by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()

    ScreenSkeleton(
        modifier = modifier,
        state = state,
        fabPadding = optionViewHeight.toDp(),
        listState = listState,
        onFabButtonClicked = onFabButtonClicked,
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
