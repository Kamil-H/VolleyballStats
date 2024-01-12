package com.kamilh.volleyballstats.presentation.features.common

import com.kamilh.volleyballstats.interactors.SynchronizeState
import com.kamilh.volleyballstats.presentation.Resources
import com.kamilh.volleyballstats.presentation.features.LinearProgressBar
import com.kamilh.volleyballstats.presentation.features.LoadingState
import com.kamilh.volleyballstats.presentation.features.Message

fun SynchronizeState.toLoadingState(hasContent: Boolean): LoadingState {
    val text = when (this) {
        is SynchronizeState.Started -> Resources.string.loading_state_started
        is SynchronizeState.UpdatingMatches -> Resources.string.loading_state_updating_matches.format(
            downloaded = downloadedMatches.size.toString(),
            allMatches = matches.size.toString(),
            season = tour.season.value.toString(),
        )
        is SynchronizeState.Error, SynchronizeState.Idle, SynchronizeState.Success -> null
    }
    val isLoading = text != null
    val linearProgressBar = when (this) {
        is SynchronizeState.UpdatingMatches -> toLinearProgressBar()
        is SynchronizeState.Started -> LinearProgressBar.Indefinite
        is SynchronizeState.Error, SynchronizeState.Idle, SynchronizeState.Success -> null
    }
    return LoadingState(
        text = text,
        showFullScreenLoading = isLoading && !hasContent,
        linearProgressBar = linearProgressBar.takeIf { hasContent },
    )
}

private fun SynchronizeState.UpdatingMatches.toLinearProgressBar(): LinearProgressBar =
    if (matches.isEmpty()) {
        LinearProgressBar.Indefinite
    } else {
        LinearProgressBar.Progress(
            value = downloadedMatches.size.toFloat() / matches.size.toFloat()
        )
    }

val SynchronizeState.isLoading: Boolean
    get() = this is SynchronizeState.Started || this is SynchronizeState.UpdatingMatches

fun SynchronizeState.toMessage(): Message? =
    if (this is SynchronizeState.Error) {
        Message(text = type.toMessageText(), buttonText = Resources.string.button_retry)
    } else null

private fun SynchronizeState.Error.Type.toMessageText(): String =
    when (this) {
        SynchronizeState.Error.Type.Connection -> Resources.string.error_connection
        SynchronizeState.Error.Type.Server -> Resources.string.error_server
        SynchronizeState.Error.Type.Unexpected -> Resources.string.error_unexpected
    }
