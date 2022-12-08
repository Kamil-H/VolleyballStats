package com.kamilh.volleyballstats.presentation.features.common

import com.kamilh.volleyballstats.interactors.SynchronizeState
import com.kamilh.volleyballstats.presentation.features.LoadingState
import com.kamilh.volleyballstats.presentation.features.Message

fun SynchronizeState.toLoadingState(hasContent: Boolean): LoadingState {
    val text = when (this) {
        is SynchronizeState.Started -> "Updating..."
        is SynchronizeState.UpdatingMatches -> "Downloading ${this.matches.size} matches in ${tour.season.value} season"
        is SynchronizeState.Error, SynchronizeState.Idle, SynchronizeState.Success -> null
    }
    val isLoading = text != null
    return LoadingState(
        text = text,
        showFullScreenLoading = isLoading && !hasContent,
        showSmallLoading = isLoading && hasContent,
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
