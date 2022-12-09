package com.kamilh.volleyballstats.presentation.features.common

import com.kamilh.volleyballstats.interactors.SynchronizeState
import com.kamilh.volleyballstats.presentation.features.LinearProgressBar
import com.kamilh.volleyballstats.presentation.features.LoadingState
import com.kamilh.volleyballstats.presentation.features.Message

fun SynchronizeState.toLoadingState(hasContent: Boolean): LoadingState {
    val text = when (this) {
        is SynchronizeState.Started -> "Updating..."
        is SynchronizeState.UpdatingMatches -> "Downloading ${downloadedMatches.size}/${matches.size} matches in ${tour.season.value} season"
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
        Message(text = type.toMessageText(), buttonText = "Retry")
    } else null

private fun SynchronizeState.Error.Type.toMessageText(): String =
    when (this) {
        SynchronizeState.Error.Type.Connection -> "No Internet connection"
        SynchronizeState.Error.Type.Server -> "Something went wrong on the server side"
        SynchronizeState.Error.Type.Unexpected -> "Something unexpected happened"
    }
