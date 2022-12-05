package com.kamilh.volleyballstats.presentation.features.home

import com.kamilh.volleyballstats.presentation.features.common.GroupedMatchItem
import com.kamilh.volleyballstats.presentation.features.players.LoadingState

data class HomeState(
    val matches: List<GroupedMatchItem> = emptyList(),
    val loadingState: LoadingState? = null,
    val showFullScreenLoading: Boolean = false,
    val showSmallLoading: Boolean = false,
    val scrollToItem: Int? = null,
    val itemToSnapTo: Int = 0,
    val onRefreshButtonClicked: () -> Unit,
    val onScrolledToItem: (Int) -> Unit,
)
