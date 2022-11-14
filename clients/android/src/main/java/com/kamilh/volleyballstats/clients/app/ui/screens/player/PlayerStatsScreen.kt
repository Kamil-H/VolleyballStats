package com.kamilh.volleyballstats.clients.app.ui.screens.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import com.kamilh.volleyballstats.presentation.features.players.LoadingState
import com.kamilh.volleyballstats.presentation.features.players.PlayerStatsPresenter
import com.kamilh.volleyballstats.presentation.features.players.PlayerStatsState
import com.kamilh.volleyballstats.ui.components.SelectOption
import com.kamilh.volleyballstats.ui.components.Table
import com.kamilh.volleyballstats.ui.extensions.toDp
import com.kamilh.volleyballstats.ui.theme.Dimens

@Composable
fun PlayerStatsScreen(
    modifier: Modifier = Modifier,
    playerStatsPresenter: PlayerStatsPresenter,
) {
    val state by playerStatsPresenter.state.collectAsState()
    PlayerStatsScreen(
        state = state,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
private fun PlayerStatsScreen(
    modifier: Modifier = Modifier,
    state: PlayerStatsState,
) {
    var optionViewHeight by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = listState.isScrollingUp() && state.showFab,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(bottom = optionViewHeight.toDp()),
                    text = { Text(text = "Filter") },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Localized description"
                        )
                    },
                    onClick = { state.onFabButtonClicked() },
                )
            }
        }
    ) {
        Column(modifier = modifier.padding(it)) {
            if (state.showFullScreenLoading) {
                FullScreenLoadingView(loadingState = state.loadingState)
            } else {
                if (state.showSmallLoading) {
                    LinearProgressIndicator()
                }
                Table(
                    modifier = Modifier.weight(1f),
                    verticalLazyListState = listState,
                    tableContent = state.tableContent,
                )
                SelectOption(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surface)
                        .onGloballyPositioned { optionViewHeight = it.size.height },
                    singleLine = true,
                    selectOptionState = state.selectSkillState,
                )
            }
        }
    }
}

@Composable
fun FullScreenLoadingView(
    modifier: Modifier = Modifier,
    loadingState: LoadingState?,
) {
    if (loadingState != null) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(Dimens.MarginExtraLarge)
        ) {
            Text(
                text = loadingState.text,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(Dimens.MarginMedium))
            LinearProgressIndicator()
        }
    }
}

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}
