package com.kamilh.volleyballstats.presentation.features.main

import com.kamilh.volleyballstats.presentation.features.Presenter
import com.kamilh.volleyballstats.presentation.features.SavableMap
import com.kamilh.volleyballstats.presentation.navigation.NavigationEvent
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

class MainPresenter private constructor(
    private val navigationEventSender: NavigationEventSender,
) : Presenter {

    private val _state: MutableStateFlow<MainState> = MutableStateFlow(
        MainState(bottomItems = bottomItems())
    )
    val state: StateFlow<MainState> = _state.asStateFlow()

    private fun bottomItems(selected: BottomMenuItem = BottomMenuItem.Home): List<BottomItemState> =
        BottomMenuItem.values().map { item ->
            BottomItemState(
                id = item,
                icon = item.icon,
                label = item.label,
                selected = item == selected,
                onClicked = ::onMenuItemClicked,
            )
        }

    private fun onMenuItemClicked(id: BottomMenuItem) {
        _state.update { state ->
            state.copy(bottomItems = state.bottomItems.select(id))
        }
        val event = when (id) {
            BottomMenuItem.Home -> NavigationEvent.HomeTabRequested
            BottomMenuItem.Players -> NavigationEvent.PlayersTabRequested
            BottomMenuItem.Teams -> NavigationEvent.TeamsTabRequested
        }
        navigationEventSender.send(event)
    }

    @Inject
    class Factory(
        private val navigationEventSender: NavigationEventSender,
    ) : Presenter.Factory<MainPresenter, Unit> {

        override fun create(
            coroutineScope: CoroutineScope,
            savableMap: SavableMap,
            extras: Unit,
        ): MainPresenter = MainPresenter(
            navigationEventSender = navigationEventSender,
        )
    }
}
