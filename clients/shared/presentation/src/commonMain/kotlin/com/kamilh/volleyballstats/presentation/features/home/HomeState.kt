package com.kamilh.volleyballstats.presentation.features.home

import com.kamilh.volleyballstats.presentation.features.ActionButton
import com.kamilh.volleyballstats.presentation.features.LoadingState
import com.kamilh.volleyballstats.presentation.features.ScreenState
import com.kamilh.volleyballstats.presentation.features.TopBarState
import com.kamilh.volleyballstats.presentation.features.common.GroupedMatchItem

data class HomeState(
    val matches: List<GroupedMatchItem> = emptyList(),
    val scrollToItem: Int? = null,
    val itemToSnapTo: Int = 0,
    val onRefreshButtonClicked: () -> Unit,
    val onScrolledToItem: (Int) -> Unit,
    override val loadingState: LoadingState = LoadingState(),
    override val topBarState: TopBarState = TopBarState(),
    override val actionButton: ActionButton = ActionButton(),
) : ScreenState
