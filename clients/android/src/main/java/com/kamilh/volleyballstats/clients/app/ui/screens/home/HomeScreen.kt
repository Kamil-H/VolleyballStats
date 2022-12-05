package com.kamilh.volleyballstats.clients.app.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.kamilh.volleyballstats.clients.app.ui.screens.player.FullScreenLoadingView
import com.kamilh.volleyballstats.presentation.features.home.HomePresenter
import com.kamilh.volleyballstats.presentation.features.home.HomeState
import com.kamilh.volleyballstats.ui.components.MatchList
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homePresenter: HomePresenter,
) {
    val state by homePresenter.state.collectAsState()
    HomeScreen(
        modifier = modifier,
        state = state,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeState,
) {

    val scrollState = rememberLazyListState()

    LaunchedEffect(state.scrollToItem) {
        state.scrollToItem?.let { scrollToItem ->
            delay(200)
            scrollState.animateScrollToItem(index = scrollToItem)
            state.onScrolledToItem(scrollToItem)
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = "Matches") },
                actions = {
                    IconButton(onClick = { state.onRefreshButtonClicked() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        }
    ) {
        Box(modifier = modifier.padding(it)) {
            if (state.showFullScreenLoading) {
                FullScreenLoadingView(loadingState = state.loadingState)
            } else {
                MatchList(
                    groupedMatchItems = state.matches,
                    itemToSnapTo = state.itemToSnapTo,
                    state = scrollState,
                )
                if (state.showSmallLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
