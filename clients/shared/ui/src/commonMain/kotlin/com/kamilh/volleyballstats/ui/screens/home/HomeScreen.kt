package com.kamilh.volleyballstats.ui.screens.home

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
    presenter: HomePresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()
    HomeScreen(
        modifier = modifier,
        state = state,
        onRetry = presenter::onRetry,
        onMessageDismissed = presenter::onMessageDismissed,
    )
}

@Composable
private fun HomeScreen(
    state: HomeState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onMessageDismissed: () -> Unit,
) {

    val scrollState = rememberLazyListState()

    LaunchedEffect(state.scrollToItem) {
        state.scrollToItem?.let { scrollToItem ->
            delay(SCROLL_TO_ITEM_DELAY_MS)
            scrollState.animateScrollToItem(index = scrollToItem)
            state.onScrolledToItem(scrollToItem)
        }
    }

    ScreenSkeleton(
        modifier = modifier,
        state = state,
        onActionButtonClicked = { state.onRefreshButtonClicked() },
        onMessageButtonClicked = onRetry,
        onMessageDismissed = onMessageDismissed,
    ) {
        MatchList(
            groupedMatchItems = state.matches,
            itemToSnapTo = state.itemToSnapTo,
            state = scrollState,
        )
    }
}

private const val SCROLL_TO_ITEM_DELAY_MS = 1000L
