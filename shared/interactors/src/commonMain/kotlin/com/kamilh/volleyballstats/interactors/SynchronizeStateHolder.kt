package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.Tour
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject

fun interface SynchronizeStateSender {

    fun send(synchronizeState: SynchronizeState)
}

interface SynchronizeStateReceiver {

    fun receive(): StateFlow<SynchronizeState>

    fun errorConsumed()
}

@Inject
@Singleton
class SynchronizeStateHolder : SynchronizeStateSender, SynchronizeStateReceiver {

    private val state = MutableStateFlow<SynchronizeState>(SynchronizeState.Idle)

    override fun send(synchronizeState: SynchronizeState) {
        state.value = synchronizeState
    }

    override fun receive(): StateFlow<SynchronizeState> =
        state.asStateFlow()

    override fun errorConsumed() {
        state.value = SynchronizeState.Idle
    }
}

sealed interface SynchronizeState {

    object Idle : SynchronizeState
    object Success : SynchronizeState
    data class Started(val league: League) : SynchronizeState
    data class UpdatingMatches(
        val tour: Tour,
        val matches: List<MatchId>,
    ) : SynchronizeState
    data class Error(val type: Type) : SynchronizeState {
        enum class Type {
            Connection, Server, Unexpected
        }
    }
}
