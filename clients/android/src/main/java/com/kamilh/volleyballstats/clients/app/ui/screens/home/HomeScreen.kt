package com.kamilh.volleyballstats.clients.app.ui.screens.home

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kamilh.volleyballstats.presentation.features.home.HomePresenter
import com.kamilh.volleyballstats.presentation.features.home.HomeState
import com.kamilh.volleyballstats.ui.components.MatchList
import com.kamilh.volleyballstats.ui.components.ScreenSkeleton
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

    ScreenSkeleton(
        modifier = modifier,
        state = state,
        onActionButtonClicked = { state.onRefreshButtonClicked() }
    ) {
        MatchList(
            groupedMatchItems = state.matches,
            itemToSnapTo = state.itemToSnapTo,
            state = scrollState,
        )
    }
}
