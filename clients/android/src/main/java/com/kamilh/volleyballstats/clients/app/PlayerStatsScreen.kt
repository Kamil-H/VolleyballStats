package com.kamilh.volleyballstats.clients.app

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import com.kamilh.volleyballstats.presentation.features.players.PlayerStatsPresenter
import com.kamilh.volleyballstats.ui.components.SelectOption
import com.kamilh.volleyballstats.ui.components.Table
import com.kamilh.volleyballstats.ui.extensions.toDp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PlayerStatsScreen(
    modifier: Modifier = Modifier,
    playerStatsPresenter: PlayerStatsPresenter,
    onFilterClicked: () -> Unit,
) {
    val state = playerStatsPresenter.state.collectAsState()
    var optionViewHeight by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = listState.isScrollingUp(),
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
                    onClick = { onFilterClicked() },
                )
            }
        }
    ) {
        Column(modifier = modifier.padding(it)) {
            Table(
                modifier = Modifier.weight(1f),
                verticalLazyListState = listState,
                tableContent = state.value.tableContent,
            )
            SelectOption(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surface)
                    .onGloballyPositioned { optionViewHeight = it.size.height },
                singleLine = true,
                selectOptionState = state.value.selectSkillState,
            )
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
