package com.kamilh.volleyballstats.presentation.features

import com.kamilh.volleyballstats.presentation.features.common.Icon

interface ScreenState {

    val loadingState: LoadingState

    val topBarState: TopBarState

    val actionButton: ActionButton
}

data class LoadingState(
    val text: String? = null,
    val showFullScreenLoading: Boolean = false,
    val showSmallLoading: Boolean = false,
)

data class TopBarState(
    val title: String? = null,
    val showToolbar: Boolean = false,
    val background: Color = Color.Default,
    val navigationButtonIcon: Icon? = null,
    val actionButtonIcon: Icon? = null,
) {
    enum class Color {
        Primary, Secondary, Default
    }
}

data class ActionButton(
    val show: Boolean = false,
    val icon: Icon? = null,
)
