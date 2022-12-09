package com.kamilh.volleyballstats.interactors

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.storage.MatchStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
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
class SynchronizeStateHolder(
    coroutineScope: CoroutineScope,
    appDispatchers: AppDispatchers,
    private val matchStorage: MatchStorage,
) : SynchronizeStateSender, SynchronizeStateReceiver {

    private val state = MutableStateFlow<SynchronizeState>(SynchronizeState.Idle)

    init {
        state.filterIsInstance<SynchronizeState.UpdatingMatches>()
            .map { it.tour }
            .distinctUntilChanged()
            .flatMapLatest { tour -> matchStorage.getMatchIdsWithReport(tour.id) }
            .onEach { matches ->
                state.update { currentState ->
                    if (currentState is SynchronizeState.UpdatingMatches) {
                        currentState.copy(downloadedMatches = currentState.matches.filter(matches::contains))
                    } else {
                        currentState
                    }
                }
            }
            .flowOn(appDispatchers.default)
            .launchIn(coroutineScope)
    }

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
        val downloadedMatches: List<MatchId> = emptyList(),
    ) : SynchronizeState

    data class Error(val type: Type) : SynchronizeState {
        enum class Type {
            Connection, Server, Unexpected
        }
    }
}
