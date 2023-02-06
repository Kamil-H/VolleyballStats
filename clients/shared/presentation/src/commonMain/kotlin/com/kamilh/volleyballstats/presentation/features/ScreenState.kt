package com.kamilh.volleyballstats.presentation.features

import com.kamilh.volleyballstats.presentation.features.common.Icon

interface ScreenState {

    val loadingState: LoadingState

    val topBarState: TopBarState

    val actionButton: ActionButton

    val message: Message?

    val colorAccent: ColorAccent
}

data class LoadingState(
    val text: String? = null,
    val showFullScreenLoading: Boolean = false,
    val linearProgressBar: LinearProgressBar? = null,
)

sealed interface LinearProgressBar {
    object Indefinite : LinearProgressBar
    data class Progress(val value: Float) : LinearProgressBar
}

data class TopBarState(
    val title: String? = null,
    val showToolbar: Boolean = false,
    val background: Color = Color.Default,
    val navigationButtonIcon: Icon? = null,
    val actionButtonIcon: Icon? = null,
) {
    enum class Color {
        Primary, Tertiary, Default
    }
}

data class ActionButton(
    val show: Boolean = false,
    val icon: Icon? = null,
)

data class Message(
    val text: String,
    val buttonText: String? = null,
)

enum class ColorAccent {
    Primary, Tertiary, Default,
}
