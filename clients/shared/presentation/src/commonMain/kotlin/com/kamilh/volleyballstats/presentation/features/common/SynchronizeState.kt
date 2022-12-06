package com.kamilh.volleyballstats.presentation.features.common

import com.kamilh.volleyballstats.interactors.SynchronizeState
import com.kamilh.volleyballstats.presentation.features.LoadingState

fun SynchronizeState.toLoadingState(hasContent: Boolean): LoadingState {
    val text = when (this) {
        is SynchronizeState.Started -> "Updating..."
        is SynchronizeState.UpdatingMatches -> "Downloading ${this.matches.size} matches in ${tour.season.value} season"
        SynchronizeState.Error, SynchronizeState.Idle, SynchronizeState.Success -> null
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
